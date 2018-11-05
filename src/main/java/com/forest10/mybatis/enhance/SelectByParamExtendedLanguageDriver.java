package com.forest10.mybatis.enhance;

import com.forest10.base.BizPreconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SelectByParam  XML自动生成驱动
 * <p>
 * select * from xxx
 * <if test="yyy != null">
 * and yyy=#{yyy}
 * </if>
 * <if test="zzz != null">
 * and zzz=#{zzz}
 * </if>
 *
 * @author Forest10
 * @date 2018/10/16 15:25
 */
public class SelectByParamExtendedLanguageDriver extends XMLLanguageDriver implements LanguageDriver {
    private static final Pattern ENTITY_PATTERN = Pattern.compile("\\(#\\{(\\w+)\\}\\)");
    private static final String PARAM_MAP = "ParamMap";

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        BizPreconditions.checkArgument(cantSupportType().negate()
            .test(parameterType), "this parameterType==>" + parameterType.getSimpleName()
            + " can`t support in this SelectByParamExtendedLanguageDriver version");
        Matcher matcher = ENTITY_PATTERN.matcher(script);
        if (matcher.find()) {
            StringBuilder ss = new StringBuilder();
            ss.append("<where>");
            for (Field field : parameterType.getDeclaredFields()) {
                String temp = "<if test=\"__field != null\">  AND __column=#{__field} </if>";
                ss.append(temp.replaceAll("__field", field.getName())
                    .replaceAll("__column", field.getName()));
            }
            ss.append("</where>");
            script = matcher.replaceAll(ss.toString());
            script = "<script>" + script + "</script>";
        }
        return super.createSqlSource(configuration, script, parameterType);
    }

    private Predicate<Class<?>> cantSupportType() {
        return type -> StringUtils.equals(type.getSimpleName(), PARAM_MAP);
    }
}