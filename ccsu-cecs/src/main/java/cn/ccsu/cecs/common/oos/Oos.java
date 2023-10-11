package cn.ccsu.cecs.common.oos;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * oos对象
 */
@Data
@Slf4j
@Component
public class Oos {
    // md5 值
    private String md5 = "";
    // 文件名字
    private String name = "";
    // 存储url
    private String dbUrl = "";
    // 保存路径
    private transient String savePath = ""; // 序列化时忽略该字段
    // 校验结果
    private Boolean result = false;
}
