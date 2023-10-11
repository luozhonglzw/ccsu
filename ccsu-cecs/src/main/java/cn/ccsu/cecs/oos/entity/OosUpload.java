package cn.ccsu.cecs.oos.entity;

import lombok.Data;

@Data
public class OosUpload {
    // 文件加密字符串
    private String md5;
    // 文件名
    private String name;
    // 存储在数据库中的url
    private String dbUrl;
    // 结果
    private boolean result;
}
