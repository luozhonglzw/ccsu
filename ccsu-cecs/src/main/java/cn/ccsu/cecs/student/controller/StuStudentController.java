package cn.ccsu.cecs.student.controller;


import cn.ccsu.cecs.bonus.vo.StuScoreDetailsVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.RedisCache;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.service.IStuStudentService;
import cn.ccsu.cecs.student.vo.StuStudentVo;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 学生表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Slf4j
@RestController
@RequestMapping("/student/stu-student")
public class StuStudentController {
    @Autowired
    private IStuStudentService stuStudentService;

    /**
     * 查询学生个人信息
     */
    @CatchException(value = "查询学生信息失败")
    @RequestMapping("/info")
    public R info(HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String id = JWTUtils.getPayload(request, "id");

        // 查询学生个人信息
        StuStudentVo stuStudentVo = stuStudentService.getStudentVo(Integer.parseInt(id));
        if (stuStudentVo == null) {
            throw new RuntimeException("用户不存在");
        }
        return R.ok().put("stuStudent", stuStudentVo);
    }

    /**
     * 查询学生成绩明细（申请表组成）
     *
     * @param request 请求
     * @return 响应
     */
    @RedisCache(key = RedisKeyConstant.STUDENT_SCORE_DETAILS_BY_YEAR_CATEGORY_LIST_KEY)
    @CatchException(value = "查询学生成绩明细失败")
    @GetMapping("/score-details")
    public R queryScoreDetails(@RequestParam("yearId") Integer yearId,
                               @RequestParam("categoryId") Integer categoryId,
                               @RequestParam("page") Integer page,
                               @RequestParam("limit") Integer limit,
                               HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String id = JWTUtils.getPayload(request, "id");

        List<StuScoreDetailsVo> stuScoreDetailsVoList =
                stuStudentService.getStuScoreDetails(yearId, categoryId, Integer.parseInt(id), page, limit);
        return R.ok().put("data", stuScoreDetailsVoList);
    }


    /**
     * 修改密码
     */
    @CatchException(value = "修改密码异常")
    @PostMapping("/update")
    public R update(@RequestBody StudentLoginVo studentLoginVo,
                    @RequestParam("oldPassword") String oldPassword,
                    HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String id = JWTUtils.getPayload(request, "id");
        String stuNumber = JWTUtils.getPayload(request, "stuNumber");
        String stuName = JWTUtils.getPayload(request, "stuName");

        // 先进行旧密码校验
        StuStudent student = stuStudentService.getById(id);
        String encryptPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!encryptPassword.equalsIgnoreCase(student.getPassword())) {
            return R.error("旧密码输入有误");
        }

        // 前端传过来的账号如果没有，这里再设置一下
        studentLoginVo.setStuNumber(stuNumber);
        studentLoginVo.setStuName(stuName);
        // 修改密码
        stuStudentService.modifyPassword(studentLoginVo);

        log.info("修改密码，user:{}", student);

        return R.ok();
    }
}
