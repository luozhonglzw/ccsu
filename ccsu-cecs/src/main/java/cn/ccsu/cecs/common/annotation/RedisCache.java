package cn.ccsu.cecs.common.annotation;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisCache {

    /**
     * 设置在redis中对应的key
     *
     * @return redis中设置的key
     */
    String key();
}
