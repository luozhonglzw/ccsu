package cn.ccsu.cecs.common.event;

import org.springframework.context.ApplicationEvent;

/**
 * 异步重置获取时间事件
 */
public class AsyncResetTimeEvent extends ApplicationEvent {
    private String type;

    public AsyncResetTimeEvent(Object source, String type) {
        super(source);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
