package cn.ccsu.cecs.student.controller;

import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.constant.UserConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.service.IStuStudentService;
import cn.ccsu.cecs.student.utils.StuUtils;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/student")
public class StuLoginController {

    @Autowired
    IStuStudentService stuStudentService;

    /**
     * 用户登录
     *
     * @param studentLoginVo 登录信息
     * @return JSON
     */
    @CatchException(value = "用户登录失败")
    @PostMapping("/login")
    public R login(@RequestBody StudentLoginVo studentLoginVo) {
        StuUtils.checkStuNumber(studentLoginVo.getStuNumber());

        boolean isLogin = stuStudentService.login(studentLoginVo);
        if (isLogin) {
            studentLoginVo.setPassword(null);

            Map<String, String> payload = new HashMap<>();
            payload.put("id", studentLoginVo.getId().toString());
            payload.put("stuNumber", studentLoginVo.getStuNumber());
            payload.put("stuName", studentLoginVo.getStuName());
            String token = JWTUtils.getToken(payload, TokenType.STUDENT_TOKEN);
            log.info("student:" + studentLoginVo + " -> 学生登录成功");

            return R.ok("用户登录成功").put("token", token);
        } else {
            throw new RuntimeException("用户名密码错误");
        }
    }

    /**
     * 用户注销
     */
    @CatchException(value = "用户注销失败")
    @GetMapping("/logout")
    public R logout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.STUDENT_LOGIN_USER);
        request.getSession().invalidate();
        return R.ok("用户注销成功");
    }
}
