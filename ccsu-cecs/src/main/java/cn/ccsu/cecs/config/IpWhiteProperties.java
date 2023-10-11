package cn.ccsu.cecs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 可以登录后台服务器的ip白名单
 */
@ConfigurationProperties(prefix = "ip-white")
@Component
@Data
public class IpWhiteProperties {
    private String[] ipList;
}
