package com.forest10.mybatis.enhance;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简化 update的XMLLanguageDriver
 *
 * @author Forest10
 * @date 2018/9/18 下午3:01
 * @see https://stackoverflow.com/questions/3428742/how-to-use-annotations-with-ibatis-mybatis-for-an-in-query/29076097
 */
public class UpdateExtendedLanguageDriver extends XMLLanguageDriver implements LanguageDriver {
    private final Pattern inPattern = Pattern.compile("\\(#\\{(\\w+)\\}\\)");

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        Matcher matcher = inPattern.matcher(script);
        if (matcher.find()) {
            StringBuffer ss = new StringBuffer();
            ss.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");

            for (Field field : parameterType.getDeclaredFields()) {
                String temp = "<if test=\"__field != null\">__field,</if>";
                ss.append(temp.replaceAll("__field", field.getName()));
            }

            ss.deleteCharAt(ss.lastIndexOf(","));
            ss.append("</trim>");
            ss.append(" <trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
            for (Field field : parameterType.getDeclaredFields()) {
                String temp = "<if test=\"__field != null\">#{__field},</if>";
                ss.append(temp.replaceAll("__field", field.getName()));
            }
            ss.append("</trim>");
            script = matcher.replaceAll(ss.toString());
            script = "<script>" + script + "</script>";
        }
        return super.createSqlSource(configuration, script, parameterType);
    }
}