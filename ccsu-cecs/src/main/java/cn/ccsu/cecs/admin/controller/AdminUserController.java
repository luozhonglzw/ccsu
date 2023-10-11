package cn.ccsu.cecs.admin.controller;

import cn.ccsu.cecs.admin.service.AdminUserService;
import cn.ccsu.cecs.admin.vo.AdminLoginVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.entity.BaseInfo;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * 管理员表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/admin-user")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * 重置学生密码为初始密码（123456）
     *
     * @param stuNumber 学生学号
     * @param request   请求
     * @return 结果
     */
    @CatchException(value = "重置密码失败")
    @PostMapping("/reset")
    public R resetPassword(@RequestParam("stuNumber") String stuNumber, HttpServletRequest request) {
        String teacherName = JWTUtils.getPayload(request, "name");
        adminUserService.resetPassword(stuNumber, teacherName);
        return R.ok("重置成功");
    }

    /**
     * 获取所有学院、年级、专业、班级的baseInfo
     */
    @CatchException(value = "获取信息异常")
    @GetMapping("/baseInfo")
    public R getBaseInfo() {
        BaseInfo baseInfo = adminUserService.getBaseInfo();

        return R.ok().put("data", baseInfo);
    }

    /**
     * 查看管理员信息
     */
    @CatchException(value = "查看管理员信息异常")
    @GetMapping("/info")
    public R info(HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");
        String username = JWTUtils.getPayload(request, "username");
        String id = JWTUtils.getPayload(request, "id");
        AdminLoginVo adminLoginVo = new AdminLoginVo(Integer.parseInt(id), username, null, name);

        return R.ok().put("teacher", adminLoginVo);
    }

    /**
     * 管理员密码修改
     */
    @CatchException(value = "管理员密码修改异常")
    @RequestMapping("/update")
    public R update(@RequestBody AdminLoginVo adminLoginVo, HttpServletRequest request) {
        String username = JWTUtils.getPayload(request, "username");
        if (!adminLoginVo.getUsername().equalsIgnoreCase(username)) {
            throw new RuntimeException("管理员信息异常");
        }
        adminUserService.modifyPassword(adminLoginVo);
        return R.ok("密码修改成功");
    }
}
