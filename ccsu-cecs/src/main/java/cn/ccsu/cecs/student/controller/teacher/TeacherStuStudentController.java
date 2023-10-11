package cn.ccsu.cecs.student.controller.teacher;

import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.service.IStuStudentService;
import cn.ccsu.cecs.student.vo.StuStudentVo;
import cn.ccsu.cecs.student.vo.teacher.SaveStudentVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 学生表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/student/stu-student")
public class TeacherStuStudentController {
    @Autowired
    private IStuStudentService stuStudentService;

    /**
     * 查询全部学生信息
     */
    @CatchException(value = "查询全部学生信息失败")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = stuStudentService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 查询单个学生信息
     *
     * @param id 学生id
     * @return 结果
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        StuStudentVo studentVo = stuStudentService.getStudentVo(id);

        return R.ok().put("studentVo", studentVo);
    }

    /**
     * 保存学生信息
     *
     * @param saveStudentVo 需要保存的学生信息
     * @return 结果
     */
    @RequestMapping("/save")
    public R save(@RequestBody SaveStudentVo saveStudentVo, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        StuStudent stuStudent = new StuStudent();
        BeanUtils.copyProperties(saveStudentVo, stuStudent);
        stuStudent.setPassword(saveStudentVo.getPassword());

        stuStudent.setCreatedAt(new Date());
        stuStudent.setCreatedBy(name);
        stuStudent.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuStudentService.save(stuStudent);

        return R.ok();
    }

    /**
     * 修改学生信息
     *
     * @param stuStudent 修改后的学生信息
     * @return 结果
     */
    @RequestMapping("/update")
    public R update(@RequestBody StuStudent stuStudent, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuStudent.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuStudent.setUpdatedAt(new Date());
        stuStudent.setUpdatedBy(name);
        stuStudentService.updateById(stuStudent);

        return R.ok();
    }

    /**
     * 删除学生信息（逻辑删除）
     *
     * @param ids 支持批量删除，学生id集合
     * @return 结果
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            stuStudentService.update(new UpdateWrapper<StuStudent>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", name)
                    .eq("id", id));
        }

        return R.ok();
    }
}
