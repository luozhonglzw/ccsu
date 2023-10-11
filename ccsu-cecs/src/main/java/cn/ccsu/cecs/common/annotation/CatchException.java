package cn.ccsu.cecs.common.annotation;


import java.lang.annotation.*;

/**
 * 自定义注解，用于方法异常捕获
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CatchException {
    String value() default "系统异常";
}
