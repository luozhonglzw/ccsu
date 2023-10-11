package cn.ccsu.cecs.common.aop;


import cn.ccsu.cecs.common.interceptor.BaseInterceptor;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 本类采用切面来使用熔断
 * 抽取进行单独处理，侵入低
 *
 * （后续开发完善）
 */
@Component
@Slf4j
@Aspect
public class ExtFusingCurrentAop {

    @Autowired
    RedisUtils redisUtils;

    @Around(value = "@annotation(cn.ccsu.cecs.common.annotation.FusingCurrent)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 熔断限流
//        fusingCurrentLimit();

        return joinPoint.proceed();
    }

    final Map<LocalDateTime, Integer> fusingRecordMap = new ConcurrentHashMap<>(1);

    // 超时时间，几秒内可接收的最大阈值
    final long OVER_TIME = 2L;

    // 并发接收的最大阈值
    final int MAX_CONCURRENT_THRESHOLD = 500;

    final int ONE_USER_MAX_CONCURRENT_THRESHOLD = 200;

    /**
     * 熔断限流
     */
//    private void fusingCurrentLimit() {
//        // 获取当前时间
//        LocalDateTime now = LocalDateTime.now();
//
//        // 判断是否处在key对应的时间区间内
//        Set<LocalDateTime> dateTimeSet = fusingRecordMap.keySet();
//        if (dateTimeSet.size() == 0) {
//            fusingRecordMap.put(now, 1);
//            return;
//        }
//
//        Iterator<LocalDateTime> localDateTimeIterator = dateTimeSet.iterator();
//        if (!localDateTimeIterator.hasNext()) {
//            return;
//        }
//        LocalDateTime key = localDateTimeIterator.next();
//
//        // 未超时
//        if (key.plusSeconds(OVER_TIME).isBefore(now)) {
//            Integer count = fusingRecordMap.get(key);
//            String stuNumber = BaseInterceptor.stuNumberThreadLocal.get();
//            String username = TeacherInterceptor.userNameThreadLocal.get();
//            // 未超过阈值
//            if (count <= MAX_CONCURRENT_THRESHOLD) {
//                if (stuNumber != null && !Objects.equals(stuNumber, "")) {
//                    calculateQueryCount(stuNumber);
//                }
//                if (username != null && !Objects.equals(username, "")) {
//                    calculateQueryCount(username);
//                }
//                fusingRecordMap.put(key, ++count);
//                return;
//            }
//
//            if (!StringUtils.isEmpty(stuNumber)) {
//                BaseInterceptor.stuNumberThreadLocal.remove();
//            }
//            if (!StringUtils.isEmpty(username)) {
//                TeacherInterceptor.userNameThreadLocal.remove();
//            }
//
//            if (getMaxQueryInfo().size() > 0) {
//                log.warn("{}内，达到查询阈值{}，存在过多非法查询，信息:{}", OVER_TIME, MAX_CONCURRENT_THRESHOLD, getMaxQueryInfo());
//            }
//            throw new RuntimeException("当前系统拥堵，请稍后重试!");
//        }
//        // 超时
//        fusingRecordMap.clear();
//        keyAndCountMap.clear();
//    }

    final Map<String, Integer> keyAndCountMap = new ConcurrentHashMap<>();

    public void calculateQueryCount(String key) {
        Integer count = keyAndCountMap.get(key);
        if (count != null) {
            keyAndCountMap.put(key, ++count);
        } else {
            keyAndCountMap.put(key, 1);
        }
    }

    public List<String> getMaxQueryInfo() {
        // 利用Map的entrySet方法，转化为list进行排序
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(keyAndCountMap.entrySet());
        // 对list排序
        entryList.sort((o1, o2) -> {
            // 正序排列，倒序反过来
            return o1.getValue() - o2.getValue();
        });
        List<String> maliceList = new CopyOnWriteArrayList<>();
        for (Map.Entry<String, Integer> entry : entryList) {
            if (entry.getValue() > ONE_USER_MAX_CONCURRENT_THRESHOLD) {
                maliceList.add(entry.getKey() + " -> queryCount: " + entry.getValue());
            }
        }
        return maliceList;
    }

}
