package cn.ccsu.cecs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "file-server")
@Component
@Data
public class FileServerProperties {
    private String ip;
    private String port;
}
