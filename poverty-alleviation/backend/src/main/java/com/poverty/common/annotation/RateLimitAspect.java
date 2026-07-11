package com.poverty.common.annotation;

import com.poverty.common.exception.BusinessException;
import com.poverty.common.utils.RedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解 AOP 实现（滑动窗口计数器）
 */
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedisUtils redisUtils;

    @Around("@annotation(com.poverty.common.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return joinPoint.proceed();
        }

        String key = rateLimit.key();
        if (key.isEmpty()) {
            key = joinPoint.getTarget().getClass().getName() + ":" + method.getName();
        }

        int time = rateLimit.time();
        int count = rateLimit.count();
        String redisKey = "rate:" + key;

        // 滑动窗口计数
        Long current = redisUtils.increment(redisKey, 1);
        if (current == 1) {
            redisUtils.expire(redisKey, time, TimeUnit.SECONDS);
        }

        if (current != null && current > count) {
            throw new BusinessException(429, rateLimit.message());
        }

        return joinPoint.proceed();
    }
}
