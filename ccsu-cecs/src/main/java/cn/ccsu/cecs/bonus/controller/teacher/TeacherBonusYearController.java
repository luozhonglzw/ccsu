package cn.ccsu.cecs.bonus.controller.teacher;

import cn.ccsu.cecs.bonus.entity.BonusYear;
import cn.ccsu.cecs.bonus.service.IBonusYearService;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.PrintTeacherInfo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.R;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学年表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/bonus/bonus-year")
public class TeacherBonusYearController {
    @Autowired
    private IBonusYearService bonusYearService;

    @Autowired
    DefaultCache defaultCache;

    /**
     * 查询所有的学年信息
     */
    @PrintTeacherInfo(value = "管理员查询所有学年信息")
    @CatchException(value = "查询所有的学年信息失败")
    @RequestMapping("/list")
    public R list() {
        List<YearVo> yearVoCache = defaultCache.getYearVoCache();

        return R.ok().put("data", yearVoCache);
    }


    /**
     * 查询学年信息
     */
    @CatchException(value = "查询学年信息失败")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        YearVo yearVo = defaultCache.getYearVo(id);

        return R.ok().put("yearVo", yearVo);
    }

    /**
     * 保存单个学年信息
     *
     * @param bonusYear 需要保存的学年信息
     * @return 结果
     */
    @CatchException(value = "保存学年表异常")
    @RequestMapping("/save")
    public R save(@RequestBody BonusYear bonusYear, HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");

        bonusYear.setCreatedBy(name);
        bonusYear.setCreatedAt(LocalDateTime.now());
        bonusYear.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        bonusYearService.save(bonusYear);

        return R.ok();
    }

    /**
     * 修改学年信息
     *
     * @param bonusYear 需要修改的学年信息
     * @return 结果
     */
    @CatchException(value = "修改学年表异常")
    @RequestMapping("/update")
    public R update(@RequestBody BonusYear bonusYear, HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");

        bonusYear.setUpdatedAt(LocalDateTime.now());
        bonusYear.setUpdatedBy(name);
        bonusYear.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        bonusYearService.updateById(bonusYear);

        return R.ok();
    }

    /**
     * 删除学年信息（逻辑删除）
     *
     * @param ids 支持批量删除，学年id集合
     * @return 结果
     */
    @CatchException(value = "删除学年表异常")
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            bonusYearService.update(new UpdateWrapper<BonusYear>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", name)
                    .eq("id", id));
        }

        return R.ok();
    }
}
