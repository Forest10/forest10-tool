package com.forest10.mybatis.interceptor;

import com.forest10.base.BizPreconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import java.util.Objects;
import java.util.Properties;

/**
 * 拦截UpdateAll或DeleteAll Sql的执行
 *
 * @author Forest10
 * @date 2018/3/16 17:07
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class BatchModifyForbiddenInterceptor implements Interceptor {


	private static final String WHERE = "WHERE";

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (invocation.getArgs()[0] instanceof MappedStatement) {
			MappedStatement mappedStatement = (MappedStatement) invocation
					.getArgs()[0];
			SqlCommandType methodType = mappedStatement
					.getSqlCommandType();
			Object parameter = invocation.getArgs()[1];
			//前置检查 SQL
			preCheckSQL(mappedStatement, parameter, methodType);
		}
		return invocation.proceed();
	}


	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}


	/**
	 * @see org.apache.ibatis.session.defaults.DefaultSqlSession
	 * 检查sql语句中是否定义了的关键字
	 */
	private void preCheckSQL(MappedStatement mappedStatement, Object parameter, SqlCommandType sqlCommandType) {
		String sql = mappedStatement.getBoundSql(parameter).getSql();
		/***之所以要区分SqlCommandType,是因为insert底层实现也是update:详见org.apache.ibatis.session.defaults.DefaultSqlSession**/
		if (Objects.equals(sqlCommandType, SqlCommandType.UPDATE) || Objects.equals(sqlCommandType, SqlCommandType.DELETE)) {
			BizPreconditions.checkArgument(StringUtils.containsIgnoreCase(sql, WHERE),
					"can`t invoke this sql:{};because it does`t include WHERE keyWord!", sql);
		}
	}

}
