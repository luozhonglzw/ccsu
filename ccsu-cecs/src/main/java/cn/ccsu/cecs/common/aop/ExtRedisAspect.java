package cn.ccsu.cecs.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Redis切面处理类
 */
@Slf4j
@Aspect
@Component
public class ExtRedisAspect {

    /**
     * 是否开启redis缓存  true开启   false关闭
     */
    @Value("${ccsu-cecs.redis.open: false}")
    private boolean open;

    @Around("execution(* cn.ccsu.cecs.common.utils.RedisUtils.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        if (open) {
            try {
                result = point.proceed();
            } catch (Exception e) {
                log.error("redis error", e);
                throw new RuntimeException("redis配置异常，请检查");
            }
        }
        return result;
    }
}
