package cn.ccsu.cecs.bonus.controller.teacher;

import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.service.IBonusCategoryService;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.R;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 加分项类别表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/bonus/bonus-category")
public class TeacherBonusCategoryController {
    @Autowired
    private IBonusCategoryService bonusCategoryService;

    /**
     * 查询所有加分项类别信息
     *
     * @param params 请求参数。支持分页查询
     * @return 结果
     */
    @CatchException(value = "查询加分项类别表异常")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = bonusCategoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 查询单个加分项类别信息
     *
     * @param id 加分项类别id
     * @return 结果
     */
    @CatchException(value = "查询加分项类别表异常")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        BonusCategory bonusCategory = bonusCategoryService.getById(id);

        return R.ok().put("bonusCategory", bonusCategory);
    }

    /**
     * 保存单个加分项类别信息
     *
     * @param bonusCategory 需要保存的加分项类别信息
     * @return 结果
     */
    @CatchException(value = "保存加分项类别表异常")
    @RequestMapping("/save")
    public R save(@RequestBody BonusCategory bonusCategory, HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");

        bonusCategory.setCreatedBy(name);
        bonusCategory.setCreatedAt(LocalDateTime.now());
        bonusCategory.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        bonusCategoryService.save(bonusCategory);

        return R.ok();
    }

    /**
     * 修改加分项类别信息
     *
     * @param bonusCategory 需要修改的加分项类别信息
     * @return 结果
     */
    @CatchException(value = "修改加分项类别表异常")
    @RequestMapping("/update")
    public R update(@RequestBody BonusCategory bonusCategory, HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");

        bonusCategory.setUpdatedAt(LocalDateTime.now());
        bonusCategory.setUpdatedBy(name);
        bonusCategory.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        bonusCategoryService.updateById(bonusCategory);

        return R.ok();
    }

    /**
     * 删除加分项类别信息（逻辑删除）
     *
     * @param ids 支持批量删除，加分项类别id集合
     * @return 结果
     */
    @CatchException(value = "删除加分项类别表异常")
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            bonusCategoryService.update(new UpdateWrapper<BonusCategory>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", name)
                    .eq("id", id));
        }

        return R.ok();
    }
}
