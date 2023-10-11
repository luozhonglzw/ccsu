package cn.ccsu.cecs.common.exception;


import cn.ccsu.cecs.common.utils.R;
import com.baomidou.kaptcha.exception.KaptchaException;
import com.baomidou.kaptcha.exception.KaptchaIncorrectException;
import com.baomidou.kaptcha.exception.KaptchaNotFoundException;
import com.baomidou.kaptcha.exception.KaptchaTimeoutException;
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
     * 处理学号校验异常
     */
    @ExceptionHandler(StuCheckFailedException.class)
    @ResponseBody
    public R handler(StuCheckFailedException s) {
        return R.error(s.getMessage());
    }

    /**
     * 处理查询信息超时异常
     */
    @ExceptionHandler(QueryTimeOutException.class)
    @ResponseBody
    public R handler(QueryTimeOutException s) {
        return R.error(s.getMessage());
    }

    /**
     * 处理加分申请表异常
     */
    @ExceptionHandler(DealBonusApplyException.class)
    @ResponseBody
    public R handler(DealBonusApplyException s) {
        return R.error(s.getMessage());
    }

    /**
     * 处理重复提交图片异常
     */
    @ExceptionHandler(RepeatSubmitException.class)
    @ResponseBody
    public R handler(RepeatSubmitException s) {
        return R.error(s.getMessage());
    }


    /**
     * 处理验证码异常
     */
    @ExceptionHandler(value = KaptchaException.class)
    @ResponseBody
    public R kaptchaExceptionHandler(KaptchaException kaptchaException) {
        if (kaptchaException instanceof KaptchaIncorrectException) {
            return R.error("验证码不正确");
        } else if (kaptchaException instanceof KaptchaNotFoundException) {
            return R.error("验证码未找到");
        } else if (kaptchaException instanceof KaptchaTimeoutException) {
            return R.error("验证码过期");
        } else {
            return R.error("验证码渲染失败");
        }
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
