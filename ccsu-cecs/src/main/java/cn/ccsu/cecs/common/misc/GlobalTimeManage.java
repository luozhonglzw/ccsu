package cn.ccsu.cecs.common.misc;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.event.AsyncGainFailedEvent;
import cn.ccsu.cecs.common.event.AsyncResetTimeEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 全局的时间管理类
 */
@Component
public class GlobalTimeManage {

    /**
     * 对于一个异步任务获取的时间（原始时长）
     */
    public static Long ORIGIN_BASE_ASYNC_TIME_INTERVAL = TimeUnit.SECONDS.toSeconds(2L);

    /**
     * 对于多个异步任务获取的时间（原始时长）
     */
    public static Long ORIGIN_BLEND_ASYNC_TIME_INTERVAL = TimeUnit.SECONDS.toSeconds(4L);

    /**
     * 对于一个异步任务获取的时间
     */
    public static Long BASE_ASYNC_TIME_INTERVAL = TimeUnit.SECONDS.toSeconds(2L);

    /**
     * 对于多个异步任务获取的时间
     */
    public static Long BLEND_ASYNC_TIME_INTERVAL = TimeUnit.SECONDS.toSeconds(4L);

    /**
     * 异步获取失败的增加时间
     */
    public static Long FAIL_ASYNC_TIME_ADD_INTERVAL = TimeUnit.SECONDS.toSeconds(1L);

    /**
     * 当前已触发的失败查询次数  map = （触发对象的全限定类名，失败次数）
     */
    private Map<Object, Integer> queryErrorCountMap = new ConcurrentHashMap<>(32);

    /**
     * 用于异步增加失败时间重试时间
     */
    @Component
    public class AsyncFail implements ApplicationListener<AsyncGainFailedEvent> {

        @Override
        public void onApplicationEvent(AsyncGainFailedEvent asyncGainFailedEvent) {
            String type = asyncGainFailedEvent.getType();
            Object source = asyncGainFailedEvent.getSource();
            if (type.equalsIgnoreCase(ProjectConstant.BASE_ASYNC)) {
                // 如果多次查询超时的话，就会修改获取时间+1
                if (queryErrorCountMap.get(source) != null
                        && queryErrorCountMap.get(source) > ProjectConstant.MAX_QUERY_ERROR_COUNT) {
                    ORIGIN_BASE_ASYNC_TIME_INTERVAL += FAIL_ASYNC_TIME_ADD_INTERVAL;
                }
                // 失败一次，下次查询时间+1
                BASE_ASYNC_TIME_INTERVAL += FAIL_ASYNC_TIME_ADD_INTERVAL;
                // 查询失败次数+1
                if (queryErrorCountMap.get(source) != null) {
                    Integer errorCount = queryErrorCountMap.get(source);
                    queryErrorCountMap.put(source, ++errorCount);
                } else {
                    queryErrorCountMap.put(source, 1);
                }
            }
            if (type.equalsIgnoreCase(ProjectConstant.BLEND_ASYNC)) {
                // 如果多次查询超时的话，就会修改获取时间+1
                if (queryErrorCountMap.get(source) != null
                        && queryErrorCountMap.get(source) > ProjectConstant.MAX_QUERY_ERROR_COUNT) {
                    ORIGIN_BLEND_ASYNC_TIME_INTERVAL += FAIL_ASYNC_TIME_ADD_INTERVAL;
                }
                BLEND_ASYNC_TIME_INTERVAL += FAIL_ASYNC_TIME_ADD_INTERVAL;
                // 查询失败次数+1
                if (queryErrorCountMap.get(source) != null) {
                    Integer errorCount = queryErrorCountMap.get(source);
                    queryErrorCountMap.put(source, ++errorCount);
                } else {
                    queryErrorCountMap.put(source, 1);
                }
            }
        }
    }

    /**
     * 用于异步重置时间
     */
    @Component
    public class AsyncReset implements ApplicationListener<AsyncResetTimeEvent> {

        @Override
        public void onApplicationEvent(AsyncResetTimeEvent asyncResetTimeEvent) {
            String type = asyncResetTimeEvent.getType();
            Object source = asyncResetTimeEvent.getSource();
            queryErrorCountMap.forEach((errorSource, count) -> {
                if (errorSource == source) {
                    if (type.equalsIgnoreCase(ProjectConstant.BASE_ASYNC)) {
                        BASE_ASYNC_TIME_INTERVAL = ORIGIN_BASE_ASYNC_TIME_INTERVAL;
                    }
                    if (type.equalsIgnoreCase(ProjectConstant.BLEND_ASYNC)) {
                        BLEND_ASYNC_TIME_INTERVAL = ORIGIN_BLEND_ASYNC_TIME_INTERVAL;
                    }
                }
            });
        }
    }

    /**
     * 重置失败次数
     */
    public void resetErrorCount() {
        queryErrorCountMap.clear();
    }
}
