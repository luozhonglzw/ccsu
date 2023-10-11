package cn.ccsu.cecs.common.aop;

import cn.ccsu.cecs.common.annotation.LocalLock;
import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.exception.RepeatSubmitException;
import cn.ccsu.cecs.common.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义AOP切面拦截我们的方法上是否有加上@LocalLock注解
 */
@Component
@Slf4j
@Aspect
public class ExtLocalLockAop {

    private static final Object LOCK_VALUE = new Object();

    // 本地锁 保证接口幂等性
    final Map<String, Object> localLockMap = new HashMap<>();

    @Around(value = "@annotation(cn.ccsu.cecs.common.annotation.LocalLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String annotationVal = getAnnotationVal(joinPoint);
        String lockName = getJoinPointLock(joinPoint);

        // 双重检查锁，保证提交的申请表仅有一份
        if (localLockMap.get(lockName) != null) {
            throw new RepeatSubmitException(annotationVal);
        } else {
            synchronized (localLockMap) {
                if (localLockMap.get(lockName) != null) {
                    throw new RepeatSubmitException(annotationVal);
                }
                localLockMap.put(lockName, LOCK_VALUE);
            }
        }
        try {
            // 执行切入方法
            return joinPoint.proceed();
        } finally {
            // 删锁
            localLockMap.remove(lockName);
        }
    }

    /**
     * 得到切入点中的 锁的 名称（这个锁在jwt中 —— 每个用户的唯一id）
     *
     * @param joinPoint 切入点信息
     * @return 锁的名称
     */
    private String getJoinPointLock(ProceedingJoinPoint joinPoint) {
        Object[] joinPointArgs = joinPoint.getArgs();
        HttpServletRequest request = null;
        for (Object pointArg : joinPointArgs) {
            if (pointArg instanceof HttpServletRequest) {
                request = (HttpServletRequest) pointArg;
                break;
            }
        }
        if (request == null) {
            throw new RuntimeException("当前用户状态异常");
        }
        String annotationType = getAnnotationType(joinPoint);
        return JWTUtils.getPayload(request, annotationType);
    }

    /**
     * 拿到自定义注解中的exceptionValue
     *
     * @param joinPoint 切入点
     * @return 自定义注解中的exceptionValue
     */
    public String getAnnotationVal(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LocalLock declaredAnnotation = signature.getMethod().getDeclaredAnnotation(LocalLock.class);
        return declaredAnnotation.exceptionValue();
    }

    /**
     * 拿到自定义注解中的type
     *
     * @param joinPoint 切入点
     * @return 自定义注解中的type
     */
    public String getAnnotationType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LocalLock declaredAnnotation = signature.getMethod().getDeclaredAnnotation(LocalLock.class);
        TokenType type = declaredAnnotation.type();
        return type.getType();
    }

}
