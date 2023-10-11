package cn.ccsu.cecs.oos.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult implements Serializable {
    private static final long serialVersionUID = 6542941452639865951L;
    public Boolean status = true;
    public String message = "";
    public Map<String, Object> data;

    public ResponseResult(Boolean status) {
        this.status = status;
    }

    public ResponseResult(Boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseResult(Map<String, Object> data) {
        this.data = data;
    }

    public ResponseResult(String message) {
        this.message = message;
    }
}