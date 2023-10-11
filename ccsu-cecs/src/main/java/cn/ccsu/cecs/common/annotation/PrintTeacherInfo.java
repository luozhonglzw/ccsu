package cn.ccsu.cecs.common.annotation;


import java.lang.annotation.*;

/**
 * 自定义注解，用于打印老师端的操作信息
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrintTeacherInfo {
    String value();
}
