package cn.ccsu.cecs.student.controller.teacher;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.entity.StuProfession;
import cn.ccsu.cecs.student.service.IStuProfessionService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * 专业表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/student/stu-profession")
public class TeacherStuProfessionController {
    @Autowired
    private IStuProfessionService stuProfessionService;

    /**
     * 查询所有专业信息
     *
     * @param params 请求参数。支持分页查询
     * @return 结果
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = stuProfessionService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 查询单个专业信息
     *
     * @param id 专业id
     * @return 结果
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        StuProfession stuProfession = stuProfessionService.getById(id);

        return R.ok().put("stuProfession", stuProfession);
    }

    /**
     * 保存某个专业信息
     *
     * @param stuProfession 需要保存的专业信息
     * @return 结果
     */
    @RequestMapping("/save")
    public R save(@RequestBody StuProfession stuProfession, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuProfession.setCreatedBy(name);
        stuProfession.setCreatedAt(LocalDateTime.now());
        stuProfession.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuProfessionService.save(stuProfession);

        return R.ok();
    }

    /**
     * 修改专业信息
     *
     * @param stuProfession 需要修改的专业信息
     * @return 结果
     */
    @RequestMapping("/update")
    public R update(@RequestBody StuProfession stuProfession, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        stuProfession.setUpdatedAt(LocalDateTime.now());
        stuProfession.setUpdatedBy(name);
        stuProfession.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        stuProfessionService.updateById(stuProfession);

        return R.ok();
    }

    /**
     * 删除专业信息（逻辑删除）
     *
     * @param ids 支持批量删除，专业id集合
     * @return 结果
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            stuProfessionService.update(new UpdateWrapper<StuProfession>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", name)
                    .eq("id", id));
        }

        return R.ok();
    }

}
