package com.forest10.mybatis.enhance;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简化Select In的XMLLanguageDriver
 *
 * @author Forest10
 * @date 2018/9/18 下午2:55
 * @see https://stackoverflow.com/questions/3428742/how-to-use-annotations-with-ibatis-mybatis-for-an-in-query/29076097
 */
public class SelectInExtendedLanguageDriver
		extends XMLLanguageDriver implements LanguageDriver {

	private final Pattern inPattern = Pattern.compile("\\(#\\{(\\w+)\\}\\)");

	@Override
	public SqlSource createSqlSource(Configuration configuration,
	                                 String script, Class<?> parameterType) {

		Matcher matcher = inPattern.matcher(script);
		if (matcher.find()) {
			script = matcher.replaceAll("(<foreach collection=\"$1\" item=\"__item\" separator=\",\" >#{__item}</foreach>)");
		}

		script = "<script>" + script + "</script>";
		return super.createSqlSource(configuration, script, parameterType);
	}
}