package cn.ccsu.cecs.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用于redis序列化对象
 */
@Data
@AllArgsConstructor
public class JsonR {
    String msg;
    Integer code;
    Object data;

    public JsonR() {
        this.msg = "success";
        this.code = 0;
        this.data = null;
    }

    public JsonR(String msg) {
        this.msg = msg;
        this.code = 0;
        this.data = null;
    }
}
