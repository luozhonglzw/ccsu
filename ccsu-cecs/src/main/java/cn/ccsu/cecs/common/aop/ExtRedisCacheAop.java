package cn.ccsu.cecs.common.aop;


import cn.ccsu.cecs.common.annotation.RedisCache;
import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.interceptor.BaseInterceptor;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.JsonR;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 本类采用切面来使用Redis缓存
 * 抽取进行单独处理，侵入低
 */
@Component
@Slf4j
@Aspect
public class ExtRedisCacheAop {

    @Autowired
    RedisUtils redisUtils;

    ThreadLocal<String> redisKeyLocal = ThreadLocal.withInitial(() -> "");

    @Around(value = "@annotation(cn.ccsu.cecs.common.annotation.RedisCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        /**
         * 整体思路：每次查询getById方法的时候，先去redis中查询，redis中没有再去数据库中查，最后再把查到的数据保存至redis中
         *  规定Redis中的key：[key=方法名:id] [value=查询到的信息]
         *
         * 分步骤：
         * 1、拿到key，前往redis中查询
         * 2、redis没有查到，前往数据库中查询
         * 3、数据库中查询，如果是null，给redis中缓存null值，过期时间为2分钟
         * 4、数据库中查询，将数据重新回写至redis
         */
        // 熔断限流
        fusingCurrentLimit(joinPoint);

        // 1.拿到key，前往redis中查询，并拿到redis的结果
        JsonR r = queryRedis(joinPoint);

        // 2、redis没有查到，放开请求，执行业务代码
        if (r == null) {
            R result = (R) joinPoint.proceed();
            // 所有返回的数据都是放在data中，data == 数据库中查询返回的对象
            Object data = result.get("data");
            if (data == null) {
                // 3、数据库中查询，如果是null，给redis中缓存null值，过期时间为2分钟  --  解决缓存击穿
                redisUtils.set(redisKeyLocal.get(), new JsonR(), 120);
                return R.ok().put("data", null);
            } else {
                // 4、数据库中查询，将数据重新回写至redis
                redisUtils.set(redisKeyLocal.get(), new JsonR("success", 0, data));
                return R.ok().put("data", data);
            }
        } else {
            // 5、redis不为null，直接返回
            return R.ok().put("data", r.getData());
        }
    }

    /**
     * 给所有的getById定义切面，所有getById方法走的逻辑都是先查redis，再查db
     */
    @Around(value = "execution(* cn.ccsu.cecs.*.service.*.getById(*))")
    public Object getByIdAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法返回值类型
        Class<?> aClass = joinPoint.getTarget().getClass();
        Method method = aClass.getMethod("getById", Serializable.class);
        Class<?> returnType = method.getReturnType();

        // 获得方法名
        String name = aClass.getName();
        String[] split = name.split("\\.");
        String className = split[split.length - 1];
        // key = aop:方法名:getById:id
        String redisKey = "aop:getById:" + className + ":" + joinPoint.getArgs()[0];

        // 1.拿到key，前往redis中查询，并拿到redis的结果
        Object r = redisUtils.get(redisKey, returnType);

        // 2、redis没有查到，放开请求，执行业务代码
        if (r == null) {
            Object result = joinPoint.proceed();
            // 所有返回的数据都是放在data中，data == 数据库中查询返回的对象
            if (result == null) {
                // 3、数据库中查询，如果是null，给redis中缓存null值，过期时间为2分钟  --  解决缓存击穿
                redisUtils.set(redisKey, null, 120);
                return null;
            } else {
                // 4、数据库中查询，将数据重新回写至redis
                redisUtils.set(redisKey, result);
                return result;
            }
        } else {
            // 5、redis不为null，直接返回
            return r;
        }
    }

    private JsonR queryRedis(ProceedingJoinPoint joinPoint) {
        StringBuilder redisKey = new StringBuilder();
        // 拿到RedisCache中的key
        String annotationKey = getAnnotationKey(joinPoint);
        redisKey.append(annotationKey);

        // 拿到所有参数，有参数的就拼起来，说明是根据指定id或者name查询的，request必包含老师账号或者学生账号，都拼起来
        HttpServletRequest request = null;
        // 拿到参数id
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Integer) {
                redisKey.append(":").append(arg);
                continue;
            }
            if (arg instanceof String) {
                redisKey.append(":").append(arg);
                continue;
            }
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
            }
        }

        TokenType studentToken = BaseInterceptor.threadLocal.get();
        TokenType teacherToken = TeacherInterceptor.threadLocal.get();

        if (request != null) {
            if (studentToken != null) {
                String stuNumber = JWTUtils.getPayload(request, "stuNumber");
                redisKey.append(":").append(stuNumber);
            } else if (teacherToken != null) {
                String username = JWTUtils.getPayload(request, "username");
                redisKey.append(":").append(username);
            }
        }

        // 保存redis的key
        redisKeyLocal.set(redisKey.toString());

        // 2.根据redisKey查询Redis（前后端规定了返回类型必定为R）
        return redisUtils.get(redisKey.toString(), JsonR.class);
    }


    /**
     * 拿到自定义注解中的exceptionValue
     *
     * @param joinPoint 切入点
     * @return 自定义注解中的exceptionValue
     */
    public String getAnnotationKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisCache declaredAnnotation = signature.getMethod().getDeclaredAnnotation(RedisCache.class);
        return declaredAnnotation.key();
    }

    private void fusingCurrentLimit(ProceedingJoinPoint joinPoint) {
    }

}
