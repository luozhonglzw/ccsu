package cn.ccsu.cecs.common.aop;

import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 定义AOP切面拦截我们的方法上是否有加上@CatchException注解
 */
@Component
@Slf4j
@Aspect
public class ExtCatchExceptionAop {

    @Around(value = "@annotation(cn.ccsu.cecs.common.annotation.CatchException)")
    public Object around(ProceedingJoinPoint joinPoint) {
        String annotationVal = getAnnotationVal(joinPoint);
        try {
            // 执行切入方法
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("@CatchException 捕获错误日志：" + throwable);
            if (throwable.getMessage() != null) {
                throw new RuntimeException(throwable.getMessage());
            } else {
                throw new RuntimeException(annotationVal);
            }
        }
    }

    /**
     * 拿到自定义注解中的value
     *
     * @param joinPoint 切入点
     * @return 自定义注解中的value
     */
    public String getAnnotationVal(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CatchException declaredAnnotation = signature.getMethod().getDeclaredAnnotation(CatchException.class);
        return declaredAnnotation.value();
    }

    /**
     * 获取上一个方法的信息
     *
     * @return 类名-方法名
     */
    public Map<Object, Object> previousStackInfo() {
        StackTraceElement[] stacks = (new Throwable()).getStackTrace();

        for (int i = 0; i < stacks.length; i++) {
            if (i == 2) {
                int orderId = i;
                return new HashMap<>(1) {{
                    put(stacks[orderId].getClassName(), stacks[orderId].getMethodName());
                }};
            }
        }
        return null;
    }
}
