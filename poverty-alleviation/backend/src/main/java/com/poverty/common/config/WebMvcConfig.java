package com.poverty.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注意：不覆盖 /** 路径，否则会禁用 Spring Boot 默认的静态资源处理器。
 * 默认已包含：classpath:/META-INF/resources/、classpath:/resources/、
 * classpath:/static/、classpath:/public/
 * Knife4j 的 doc.html 在 classpath:/META-INF/resources/ 中，由默认处理器提供服务。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 生产模式下前端构建产物放在 classpath:/static/，Spring Boot 默认会扫描。
        // 这里不覆盖 /**，保留默认行为。
        // 如需自定义 SPA 路径回退，使用单独的路径前缀而非 /**
    }
}
