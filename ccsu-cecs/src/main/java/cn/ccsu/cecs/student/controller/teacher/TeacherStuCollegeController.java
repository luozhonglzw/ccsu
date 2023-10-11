package cn.ccsu.cecs.student.controller.teacher;

import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.entity.StuCollege;
import cn.ccsu.cecs.student.service.IStuCollegeService;
import cn.ccsu.cecs.student.vo.CollegeVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 学院表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/student/stu-college")
public class TeacherStuCollegeController {
    @Autowired
    private IStuCollegeService stuCollegeService;

    /**
     * 查询所有学院信息
     *
     * @param params 请求参数。支持分页查询
     * @return 结果
     */
    @CatchException(value = "查询学院信息失败")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = stuCollegeService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 查询单个学院信息
     *
     * @param id 学院id
     * @return 结果
     */
    @CatchException(value = "查询学院信息失败")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        CollegeVo collegeVo = stuCollegeService.getCollegeVo(id);

        return R.ok().put("collegeVo", collegeVo);
    }

    /**
     * 保存单个学院信息
     *
     * @param stuCollege 需要保存的学院信息
     * @return 结果
     */
    @CatchException(value = "保存学院信息失败")
    @RequestMapping("/save")
    public R save(@RequestBody StuCollege stuCollege, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuCollege.setCreatedBy(name);
        stuCollege.setCreatedAt(LocalDateTime.now());
        stuCollege.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuCollegeService.save(stuCollege);

        return R.ok();
    }

    /**
     * 修改学院信息
     *
     * @param stuCollege 需要修改的学院信息
     * @return 结果
     */
    @CatchException(value = "修改学院信息失败")
    @RequestMapping("/update")
    public R update(@RequestBody StuCollege stuCollege, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuCollege.setUpdatedAt(LocalDateTime.now());
        stuCollege.setUpdatedBy(name);
        stuCollege.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuCollegeService.updateById(stuCollege);

        return R.ok();
    }

    /**
     * 删除学院信息（逻辑删除）
     *
     * @param ids 支持批量删除，学院id集合
     * @return 结果
     */
    @CatchException(value = "删除学院信息失败")
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            stuCollegeService.update(new UpdateWrapper<StuCollege>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", name)
                    .eq("id", id));
        }

        return R.ok();
    }
}
