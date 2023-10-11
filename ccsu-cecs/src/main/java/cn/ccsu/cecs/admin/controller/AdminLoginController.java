package cn.ccsu.cecs.admin.controller;

import cn.ccsu.cecs.admin.service.AdminUserService;
import cn.ccsu.cecs.admin.utils.AdminUtils;
import cn.ccsu.cecs.admin.vo.AdminLoginVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.PrintTeacherInfo;
import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.constant.UserConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员登录模块
 */
@RestController
@RequestMapping("/teacher")
public class AdminLoginController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * 管理员登录
     *
     * @param adminLoginVo 登录新
     * @return 结果
     */
    @CatchException(value = "管理员登录异常")
    @PostMapping("/login")
    public R login(@RequestBody AdminLoginVo adminLoginVo) throws UnknownHostException {
        // 1.检查管理员账号
        AdminUtils.checkAdminAccount(adminLoginVo.getUsername());
        // 2.登录
        adminUserService.login(adminLoginVo);
        // 3.返回token令牌
        Map<String, String> map = new HashMap<>();
        map.put("id", adminLoginVo.getId().toString());
        map.put("name", adminLoginVo.getName());
        map.put("username", adminLoginVo.getUsername());

        String token = JWTUtils.getToken(map, TokenType.TEACHER_TOKEN);
        return R.ok("管理员登录成功").put("token", token);
    }

    @GetMapping("/logout")
    public R logout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.TEACHER_LOGIN_USER);
        request.getSession().invalidate();
        return R.ok("管理员注销成功");
    }
}
