package com.poverty.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /** 限流 Key（默认为 类名:方法名） */
    String key() default "";

    /** 时间窗口（秒），默认 10 秒 */
    int time() default 10;

    /** 时间窗口内允许的最大请求数 */
    int count() default 5;

    /** 限流提示消息 */
    String message() default "请求过于频繁，请稍后再试";
}
