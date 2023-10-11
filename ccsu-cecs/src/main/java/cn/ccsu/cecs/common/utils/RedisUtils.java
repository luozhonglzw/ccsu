package cn.ccsu.cecs.common.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis采用策略：先更新后删除
 */
@Slf4j
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ValueOperations<String, String> valueOperations;

    @Autowired
    private HashOperations<String, String, Object> hashOperations;

    @Autowired
    private ListOperations<String, Object> listOperations;

    @Autowired
    private SetOperations<String, Object> setOperations;

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    @Value("${ccsu-cecs.redis.open: false}")
    private boolean open;

    /**
     * 默认过期时长，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1;

    public final static Gson gson = new Gson();

    @PostConstruct
    public void test() {
        System.out.println(open);
    }

    public void set(String key, Object value, long expire) {
        valueOperations.set(key, toJson(value));
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    public <T> T get(String key, Class<T> clazz, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    public String get(String key, long expire) {
        String value = valueOperations.get(key);
        if (expire != NOT_EXPIRE) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    public String get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 删除特定前缀的key
     */
    public void deleteSpecialPrefix(String key) {
        Set<String> keys = redisTemplate.keys(key + "*");
        if (keys != null) {
            redisTemplate.delete(keys);
            log.info("删除缓存，key:{}", key);
        }
    }

    public void batchDelete(Collection<String> redisKeys) {
        redisTemplate.delete(redisKeys);
    }

    /**
     * Object转成JSON数据
     */
    public String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * 给redis中设置hash，指定key的值
     */
    public void hashOperationSet(String key, String hashKey, Object hashData) {
        String data = gson.toJson(hashData);
        hashOperations.put(key, hashKey, data);
        log.info("添加缓存，key:{}, hashKey:{}", key, hashKey);
    }

    /**
     * 从redis中读取hash，指定key的值
     */
    public <T> T hashOperationGet(String key, String hashKey, Class<T> clazz) {
        String hashData = (String) hashOperations.get(key, hashKey);
        return gson.fromJson(hashData, clazz);
    }

    /**
     * 从redis中读取hash，指定key的值
     */
    public <T> List<T> hashOperationMultiGet(String key, Class<T> clazz) {
        List<Object> values = hashOperations.values(key);
        List<T> result = new ArrayList<>();
        for (Object value : values) {
            result.add(gson.fromJson(String.valueOf(value), clazz));
        }
        return result;
    }

    /**
     * 判断redis中的key是否存在
     *
     * @param redisKey
     * @return
     */
    public boolean existKey(String redisKey) {
        Boolean isExist = redisTemplate.hasKey(redisKey);
        return isExist != null && isExist;
    }


    /**
     * 删除hash中的key键
     */
    public void hashOperationDelHashKey(String key, String hashKey) {
        hashOperations.delete(key, hashKey);
        log.info("删除缓存，key:{}", key);
    }
}
