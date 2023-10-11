package cn.ccsu.cecs.yangjr;

import org.springframework.context.ApplicationEvent;

public class YangEvent extends ApplicationEvent {

    private Resource resource;

    public YangEvent(Object source, Resource resource) {
        super(source);
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }
}
