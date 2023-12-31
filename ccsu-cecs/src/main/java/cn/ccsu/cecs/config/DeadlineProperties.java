package cn.ccsu.cecs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "dead-line-submit-time")
@Component
@Data
public class DeadlineProperties {
    private String time;
}
