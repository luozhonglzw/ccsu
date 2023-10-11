package cn.ccsu.cecs.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * 异步获取失败事件
 */
public class AsyncGainFailedEvent extends ApplicationEvent {

    private String type;

    public AsyncGainFailedEvent(Object source, String type) {
        super(source);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
