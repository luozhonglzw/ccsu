package cn.ccsu.cecs.kaptcha.controller;

import com.baomidou.kaptcha.Kaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kaptcha")
@CrossOrigin
public class KaptchaController {

    @Autowired
    private Kaptcha kaptcha;

    /**
     * 获取验证码图片   直接请求即可，内部已将数据写回response
     */
    @GetMapping("/render")
    public void render() {
        kaptcha.render();
    }

    /**
     * 验证码校验
     *
     * @param code 验证码
     */
    @PostMapping("/valid")
    public void validDefaultTime(@RequestParam("code") String code) {
        //default timeout 900 seconds
        kaptcha.validate(code);
    }

    /**
     * 自定义验证码有效时间
     *
     * @param code 验证码
     */
    @PostMapping("/validTime")
    public void validCustomTime(@RequestParam("code") String code) {
        kaptcha.validate(code, 60);
    }

}