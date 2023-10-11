package cn.ccsu.cecs.common.annotation;

import cn.ccsu.cecs.common.constant.TokenType;

import java.lang.annotation.*;

/**
 * 自定义注解，用于给方法加本地锁（保证接口幂等性）
 * <p>
 * 通过注解实现本地锁功能
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalLock {
    /**
     * 加锁失败的提示消息
     *
     * @return 提示消息
     */
    String exceptionValue();

    /**
     * 获取的token类型
     *
     * @return 老师token亦或学生token
     */
    TokenType type();
}
