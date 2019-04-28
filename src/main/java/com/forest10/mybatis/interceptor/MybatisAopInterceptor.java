package com.forest10.mybatis.interceptor;

import com.forest10.mybatis.annotation.SelectAllPermit;
import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Forest10
 * @date 2018/8/6 上午11:09
 * <p>
 * 相当于一个 mybatis的 Aop拦截器, 前置拦截全部查询(现在只有这一个), 后置记录增删改查的大小然后决定是否报警或者别的操作
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
        Object.class})})
@Slf4j
public class MybatisAopInterceptor implements Interceptor {

    private static final String WHERE = "WHERE";
    private int largeInsertSize = 200;
    private int largeDeleteSize = 200;
    private int largeUpdateSize = 200;
    private int largeSelectSize = 1000;

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = obj.toString();
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat
                .getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = formatter.format(obj);
        } else {
            value = Objects.isNull(obj) ? StringUtils.EMPTY : obj.toString();
        }
        return wrapParam(value);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    private static String wrapParam(String str) {
        return "'" + str + "'";
    }

    /**
     * 将问号替换成实际的值
     */
    private static String replaceInterrogationWithRealParam(Configuration configuration,
        BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        boolean predicateParameter =
            CollectionUtils.isNotEmpty(parameterMappings) && Objects.nonNull(parameterObject);
        if (!predicateParameter) {
            return sql;
        }
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
        } else {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                if (metaObject.hasGetter(propertyName)) {
                    Object obj = metaObject.getValue(propertyName);
                    sql = sql.replaceFirst("\\?", getParameterValue(obj));
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    Object obj = boundSql.getAdditionalParameter(propertyName);
                    sql = sql.replaceFirst("\\?", getParameterValue(obj));
                } else {
                    sql = sql.replaceFirst("\\?", wrapParam(StringUtils.EMPTY));
                }
            }
        }
        log.info("realSql===>{}", sql);
        return sql;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = null;
        if (invocation.getArgs()[0] instanceof MappedStatement) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
            //前置处理dao层特殊注解
            preHandleAnnotation(mappedStatement, parameter, sqlCommandType);
            result = invocation.proceed();
            //后置记录返回值大小
            afterMonitorResult(result, mappedStatement, parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            replaceInterrogationWithRealParam(configuration, boundSql);
        }
        return Objects.isNull(result) ? invocation.proceed() : result;
    }

    @Override
    public void setProperties(Properties properties) {
        this.largeInsertSize =
            Integer.parseInt(
                properties.getProperty("largeInsertSize", String.valueOf(largeInsertSize)));
        this.largeDeleteSize =
            Integer.parseInt(
                properties.getProperty("largeDeleteSize", String.valueOf(largeDeleteSize)));
        this.largeUpdateSize =
            Integer.parseInt(
                properties.getProperty("largeUpdateSize", String.valueOf(largeUpdateSize)));
        this.largeSelectSize =
            Integer.parseInt(
                properties.getProperty("largeSelectSize", String.valueOf(largeSelectSize)));
    }

    /**
     * 处理dao层注解
     */
    private void preHandleAnnotation(MappedStatement mappedStatement, Object parameter,
        SqlCommandType sqlCommandType)
        throws ClassNotFoundException {
        String namespace = mappedStatement.getId();
        String className = namespace.substring(0, namespace.lastIndexOf("."));
        String methodName = namespace.substring(namespace.lastIndexOf(".") + 1);
        //1.拿到所有公用（public）方法
        Method[] ms = Class.forName(className)
            .getMethods();
        //2.拿到SQL和sqlCommandType
        String sql = mappedStatement.getBoundSql(parameter)
            .getSql();
        for (Method m : ms) {
            //3.找到符合条件的方法
            if (m.getName()
                .equals(methodName)) {
                //3.1 处理SELECT
                if (Objects.equals(sqlCommandType, SqlCommandType.SELECT)) {
                    //3.1 处理允许SELECT_ALL
                    SelectAllPermit selectAllPermit = m.getAnnotation(SelectAllPermit.class);
                    if (Objects.isNull(selectAllPermit)) {
                        Preconditions.checkArgument(StringUtils.containsIgnoreCase(sql, WHERE),
                            "can`t invoke this method,because SELECT_ALL IS Forbidden!if you confirm this method can be selectAll,add SelectAllPermit above it!");
                    }
                }
            }
        }
    }

    /**
     * 记录返回值(如果是)Collection大小
     */
    private void afterMonitorResult(Object result, MappedStatement mappedStatement,
        Object parameter) {
        //do Monitor
    }

    private void logSql(MappedStatement mappedStatement, Object parameter, String sql, double size,
        int threshold) {
        if (size >= threshold) {
            log.error("LARGE SIZE SQL INFO:method=>{},sql=>{},parameter=>{},size=>{}",
                mappedStatement.getId(), sql,
                parameter, size);
        }
    }
}