package cn.ccsu.cecs.student.controller.teacher;

import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.entity.StuClass;
import cn.ccsu.cecs.student.service.IStuClassService;
import cn.ccsu.cecs.student.vo.ClassVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * 班级表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/student/stu-class")
public class TeacherStuClassController {
    @Autowired
    private IStuClassService stuClassService;

    /**
     * 查询所有班级信息
     *
     * @param params 请求参数。支持分页查询
     * @return 结果
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = stuClassService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 查询单个班级信息
     *
     * @param id 班级id
     * @return 结果
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        ClassVo classVo = stuClassService.getClassVo(id);

        return R.ok().put("classVo", classVo);
    }

    /**
     * 保存单个班级信息
     *
     * @param stuClass 需要保存的班级信息
     * @return 结果
     */
    @CatchException(value = "保存单个班级信息失败")
    @RequestMapping("/save")
    public R save(@RequestBody StuClass stuClass, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuClass.setCreatedBy(name);
        stuClass.setCreatedAt(LocalDateTime.now());
        stuClass.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuClassService.save(stuClass);

        return R.ok();
    }

    /**
     * 修改班级信息
     *
     * @param stuClass 需要修改的班级信息
     * @return 结果
     */
    @RequestMapping("/update")
    public R update(@RequestBody StuClass stuClass, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuClass.setUpdatedAt(LocalDateTime.now());
        stuClass.setUpdatedBy(name);
        stuClass.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuClassService.updateById(stuClass);

        return R.ok();
    }

    /**
     * 删除班级信息（逻辑删除）
     *
     * @param ids 支持批量删除，班级id集合
     * @return 结果
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            stuClassService.update(new UpdateWrapper<StuClass>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", name)
                    .eq("id", id));
        }

        return R.ok();
    }

}
