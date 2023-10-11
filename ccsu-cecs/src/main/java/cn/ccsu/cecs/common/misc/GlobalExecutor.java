package cn.ccsu.cecs.common.misc;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.event.AsyncGainFailedEvent;
import cn.ccsu.cecs.common.event.AsyncResetTimeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 全局任务执行器
 */
@Component
@Slf4j
public class GlobalExecutor {

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 获取异步Future结果，并做好异常处理
     *
     * @param future     异步对象
     * @param failureMsg 失败提示消息
     * @param <T>        泛型
     * @return 泛型
     */
    public <T> T getAsync(Future<T> future, String asyncType, String failureMsg) {
        T t;
        String className = previousClassName();
        try {
            if (asyncType.equalsIgnoreCase(ProjectConstant.BASE_ASYNC)) {
                t = future.get(GlobalTimeManage.BASE_ASYNC_TIME_INTERVAL, TimeUnit.SECONDS);
                // 发布重置时间事件
                this.applicationContext.publishEvent(new AsyncResetTimeEvent(className, ProjectConstant.BASE_ASYNC));
            } else {
                t = future.get(GlobalTimeManage.BLEND_ASYNC_TIME_INTERVAL, TimeUnit.SECONDS);
                // 异步发布重置时间事件
                this.applicationContext.publishEvent(new AsyncResetTimeEvent(className, ProjectConstant.BLEND_ASYNC));
            }
        } catch (InterruptedException | ExecutionException e) {
            // 获取栈方法信息
            Map<Object, Object> methodInfoMap = previousStackInfo();
            if (methodInfoMap != null) {
                methodInfoMap.forEach((cName, methodName) -> {
                    log.warn("{} - {}() is exception", cName, methodName);
                });
            }
            throw new RuntimeException(failureMsg + "失败");
        } catch (TimeoutException e) {
            // 获取栈方法信息
            Map<Object, Object> methodInfoMap = previousStackInfo();
            if (methodInfoMap != null) {
                methodInfoMap.forEach((cName, methodName) -> {
                    log.warn("{} - {}() is timeout", cName, methodName);
                });
            }

            // 发布失败重试事件
            if (asyncType.equalsIgnoreCase(ProjectConstant.BASE_ASYNC)) {
                this.applicationContext.publishEvent(new AsyncGainFailedEvent(className, ProjectConstant.BASE_ASYNC));
            } else {
                this.applicationContext.publishEvent(new AsyncGainFailedEvent(className, ProjectConstant.BLEND_ASYNC));
            }
            throw new RuntimeException(failureMsg + "超时");
        }
        return t;
    }

    /**
     * 获取上一个方法的信息
     *
     * @return 类名-方法名
     */
    public static Map<Object, Object> previousStackInfo() {
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

    /**
     * 获取上一个方法的类名
     *
     * @return 上一个方法类名
     */
    public static String previousClassName() {
        return (String) Objects.requireNonNull(previousStackInfo()).keySet().toArray()[0];
    }
}
