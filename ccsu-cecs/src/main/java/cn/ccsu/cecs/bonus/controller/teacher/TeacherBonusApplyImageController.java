package cn.ccsu.cecs.bonus.controller.teacher;

import cn.ccsu.cecs.bonus.service.IBonusApplyImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 申请材料表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/bonus/bonus-apply-image")
public class TeacherBonusApplyImageController {
    @Autowired
    private IBonusApplyImageService bonusApplyImageService;

//    /**
//     * 列表
//     */
//    @CatchException(value = "查询申请材料表异常")
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params) {
//        PageUtils page = bonusApplyImageService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }
//
//
//    /**
//     * 信息
//     */
//    @CatchException(value = "查询申请材料表异常")
//    @RequestMapping("/info/{id}")
//    public R info(@PathVariable("id") Integer id) {
//        BonusApplyImage bonusApplyImage = bonusApplyImageService.getById(id);
//
//        return R.ok().put("bonusApplyImage", bonusApplyImage);
//    }
//
//    /**
//     * 保存
//     */
//    @CatchException(value = "保存申请材料表异常")
//    @RequestMapping("/save")
//    public R save(@RequestBody BonusApplyImage bonusApplyImage) {
//        bonusApplyImageService.save(bonusApplyImage);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @CatchException(value = "修改申请材料表异常")
//    @RequestMapping("/update")
//    public R update(@RequestBody BonusApplyImage bonusApplyImage) {
//        bonusApplyImageService.updateById(bonusApplyImage);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @CatchException(value = "删除申请材料表异常")
//    @RequestMapping("/delete")
//    public R delete(@RequestBody Integer[] ids) {
//        bonusApplyImageService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
