package cn.ccsu.cecs.common.aop;


import cn.ccsu.cecs.admin.entity.AdminUser;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.PrintTeacherInfo;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 本类采用切面来打印老师登录信息日志
 *
 * 抽取进行单独处理，侵入低
 */
@Component
@Slf4j
@Aspect
public class ExtPrintTeacherInfoAop {

    @Before(value = "@annotation(cn.ccsu.cecs.common.annotation.PrintTeacherInfo)")
    public void before(JoinPoint joinPoint) {
        String annotationVal = getAnnotationVal((ProceedingJoinPoint) joinPoint);
        String adminUser = TeacherInterceptor.teacherInfoThreadLocal.get();
        log.info("管理员信息：" + adminUser + " -> " + annotationVal);
        TeacherInterceptor.teacherInfoThreadLocal.remove();
    }

    /**
     * 拿到自定义注解中的value
     *
     * @param joinPoint 切入点
     * @return 自定义注解中的value
     */
    public String getAnnotationVal(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        PrintTeacherInfo declaredAnnotation = signature.getMethod().getDeclaredAnnotation(PrintTeacherInfo.class);
        return declaredAnnotation.value();
    }
}
