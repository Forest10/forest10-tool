package forest10.mybatis.interceptor;

import com.google.common.base.Preconditions;
import forest10.mybatis.annotation.SelectAllPermit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Forest10
 * @date 2018/8/6 上午11:09
 * <p>
 * 相当于一个 mybatis的 Aop拦截器,
 * 前置拦截全部查询(现在只有这一个),
 * 后置记录增删改查的大小然后决定是否报警或者别的操作
 */
@Intercepts({
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class
		}),
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class
		}),
		@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class MybatisAopInterceptor implements Interceptor {


	private static final String WHERE = "WHERE";
	private int largeInsertSize = 200;
	private int largeDeleteSize = 200;
	private int largeUpdateSize = 200;
	private int largeSelectSize = 1000;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object result = null;
		if (invocation.getArgs()[0] instanceof MappedStatement) {
			MappedStatement mappedStatement = (MappedStatement) invocation
					.getArgs()[0];
			Object parameter = invocation.getArgs()[1];
			SqlCommandType sqlCommandType = mappedStatement
					.getSqlCommandType();
			//前置处理dao层特殊注解
			preHandleAnnotation(mappedStatement, parameter, sqlCommandType);
			result = invocation.proceed();
			//后置记录返回值大小
			afterMonitorResult(result, mappedStatement, parameter);
		}
		return Objects.isNull(result) ? invocation.proceed() : result;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		this.largeInsertSize = Integer.parseInt(properties.getProperty("largeInsertSize", String.valueOf(largeInsertSize)));
		this.largeDeleteSize = Integer.parseInt(properties.getProperty("largeDeleteSize", String.valueOf(largeDeleteSize)));
		this.largeUpdateSize = Integer.parseInt(properties.getProperty("largeUpdateSize", String.valueOf(largeUpdateSize)));
		this.largeSelectSize = Integer.parseInt(properties.getProperty("largeSelectSize", String.valueOf(largeSelectSize)));
	}


	/**
	 * 处理dao层注解
	 *
	 * @param mappedStatement
	 * @param parameter
	 * @throws ClassNotFoundException
	 */
	private void preHandleAnnotation(MappedStatement mappedStatement, Object parameter, SqlCommandType sqlCommandType) throws ClassNotFoundException {
		String namespace = mappedStatement.getId();
		String className = namespace.substring(0, namespace.lastIndexOf("."));
		String methodName = namespace.substring(namespace.lastIndexOf(".") + 1);
		//1.拿到所有公用（public）方法
		Method[] ms = Class.forName(className).getMethods();
		//2.拿到SQL和sqlCommandType
		String sql = mappedStatement.getBoundSql(parameter).getSql();
		for (Method m : ms) {
			//3.找到符合条件的方法
			if (m.getName().equals(methodName)) {
				//3.1 处理SELECT
				if (Objects.equals(sqlCommandType, SqlCommandType.SELECT)) {
					//3.1 处理允许SELECT_ALL
					SelectAllPermit selectAllPermit = m.getAnnotation(SelectAllPermit.class);
					if (Objects.isNull(selectAllPermit)) {
						Preconditions.checkArgument(StringUtils.containsIgnoreCase(sql, WHERE), selectAllPermit.message());
					}
				}
			}
		}
	}


	/**
	 * 记录返回值(如果是)Collection大小
	 */
	private void afterMonitorResult(Object result, MappedStatement mappedStatement, Object parameter) {
		//do Monitor
	}

	private void logSql(MappedStatement mappedStatement, Object parameter, String sql, double size, int threshold) {
		if (size >= threshold) {
			log.error("LARGE SIZE SQL INFO:method=>{},sql=>{},parameter=>{},size=>{}", mappedStatement.getId(), sql, parameter, size);
		}
	}
}