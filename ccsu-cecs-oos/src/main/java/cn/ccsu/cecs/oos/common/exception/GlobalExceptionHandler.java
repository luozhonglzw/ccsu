package cn.ccsu.cecs.oos.common.exception;


import cn.ccsu.cecs.oos.common.utils.R;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理数据校验异常
     *
     * @param e 异常
     * @return 结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public R handlerValidException(MethodArgumentNotValidException e) {
        Map<String, String> map = new HashMap<>();

        BindingResult bindingResult = e.getBindingResult();
        bindingResult.getFieldErrors().forEach((fieldError -> {
            map.put(fieldError.getField(), fieldError.getDefaultMessage());
        }));

        return R.error("数据校验异常").put("data", map);
    }

    /**
     * 处理文件IO异常
     */
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public R handler(IOException ioe) {
        return R.error(ioe.getMessage());
    }


    /**
     * 处理运行时异常-兜底
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R handler(RuntimeException s) {
        return R.error(s.getMessage());
    }

}
