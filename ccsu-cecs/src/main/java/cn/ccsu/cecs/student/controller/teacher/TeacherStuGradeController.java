package cn.ccsu.cecs.student.controller.teacher;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.entity.StuGrade;
import cn.ccsu.cecs.student.service.IStuGradeService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * 年级表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/student/stu-grade")
public class TeacherStuGradeController {
    @Autowired
    private IStuGradeService stuGradeService;

    /**
     * 查询所有年级信息
     *
     * @param params 请求参数。支持分页查询
     * @return 结果
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = stuGradeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 查询单个年级信息
     *
     * @param id 年级id
     * @return 结果
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        StuGrade stuGrade = stuGradeService.getById(id);

        return R.ok().put("stuGrade", stuGrade);
    }

    /**
     * 保存单个年级信息
     *
     * @param stuGrade 需要保存的年级信息
     * @return 结果
     */
    @RequestMapping("/save")
    public R save(@RequestBody StuGrade stuGrade, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuGrade.setCreatedBy(name);
        stuGrade.setCreatedAt(LocalDateTime.now());
        stuGrade.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuGradeService.save(stuGrade);

        return R.ok();
    }

    /**
     * 修改年级信息
     *
     * @param stuGrade 需要修改的年级信息
     * @return 结果
     */
    @RequestMapping("/update")
    public R update(@RequestBody StuGrade stuGrade, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuGrade.setUpdatedAt(LocalDateTime.now());
        stuGrade.setUpdatedBy(name);
        stuGrade.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuGradeService.updateById(stuGrade);

        return R.ok();
    }

    /**
     * 删除年级信息（逻辑删除）
     *
     * @param ids 支持批量删除，年级id集合
     * @return 结果
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            stuGradeService.update(new UpdateWrapper<StuGrade>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", name)
                    .eq("id", id));
        }

        return R.ok();
    }
}
