package com.forest10.web.conf;

import com.forest10.web.annotation.EnableLogFilter;
import com.forest10.web.filter.realize.LogFilter;
import com.google.common.collect.Lists;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

/**
 * @author Forest10
 * @date 2018/9/19 下午2:38
 */
@Configuration
public class LogFilterConf implements ImportAware {

    private String[] excludeUris;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        AnnotationAttributes enableLogFilter = AnnotationAttributes.fromMap(
            importMetadata.getAnnotationAttributes(EnableLogFilter.class.getName(), false));
        Assert.notNull(enableLogFilter,
            "@EnableLogFilter is not present on importing class " + importMetadata.getClassName());
        this.excludeUris = enableLogFilter.getStringArray("excludeUris");
    }

    @Bean
    public FilterRegistrationBean logFilterProxy() {
        LogFilter logFilter = new LogFilter();
        logFilter.setExcludeUri(excludeUris);
        FilterRegistrationBean<LogFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(logFilter);
        filter.setUrlPatterns(Lists.newArrayList("/*"));
        filter.setMatchAfter(true);
        return filter;
    }

}