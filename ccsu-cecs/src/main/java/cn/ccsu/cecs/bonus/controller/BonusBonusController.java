package cn.ccsu.cecs.bonus.controller;


import cn.ccsu.cecs.bonus.service.IBonusBonusService;
import cn.ccsu.cecs.bonus.vo.teacher.BonusBonusVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.RedisCache;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 加分项表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@RestController
@RequestMapping("/student/bonus/bonus-bonus")
public class BonusBonusController {
    @Autowired
    private IBonusBonusService bonusBonusService;

    @Autowired
    IOosImagesService oosImagesService;


    /**
     * 根据加分类别来获取所有可以的加分名称
     *
     * @param categoryId 类别id
     * @return 加分名称
     */
    @RedisCache(key = RedisKeyConstant.STUDENT_BONUS_NAME_LIST_SINGLE_KEY)
    @CatchException(value = "获取加分项名称异常")
    @GetMapping("/getBonusName")
    public R getBonusName(@RequestParam("categoryId") Integer categoryId) {
        // 获取指定加分类别的所有加分项名称
        List<String> bonusNames = bonusBonusService.getBonusName(categoryId);
        return R.ok().put("data", bonusNames);
    }

    /**
     * 根据类别id、加分项名称获取加分项的信息
     *
     * @param categoryId 类别id
     * @param bonusName  加分项名称
     * @return 结果
     */
    @RedisCache(key = RedisKeyConstant.STUDENT_BONUS_INFO_ONE_SINGLE_KEY)
    @CatchException(value = "获取加分项信息异常")
    @GetMapping("/getBonusInfo")
    public R getBonusInfo(@RequestParam("categoryId") Integer categoryId,
                          @RequestParam("name") String bonusName) {

        // 根据类别id、加分项名称获取加分项的信息
        BonusBonusVo bonusBonusVo = bonusBonusService.getBonusInfo(categoryId, bonusName);
        return R.ok().put("data", bonusBonusVo);
    }

    @CatchException(value = "查询加分项表异常")
    @GetMapping("/search")
    public R search(@RequestParam("name") String bonusName,
                    @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        List<BonusBonusVo> bonusBonusVos;
        if (categoryId == null) {
            bonusBonusVos = bonusBonusService.searchByName(bonusName);
        } else {
            // 根据名称/类别id 查询加分项信息
            bonusBonusVos = bonusBonusService.searchByNameAndCategoryId(bonusName, categoryId);
        }
        return R.ok().put("data", bonusBonusVos);
    }


    /**
     * 已转至文件服务器
     */
//    /**
//     * 获取加分项图片信息
//     */
//    @GetMapping("/look-bonus-image")
//    public void lookBonusImage(@RequestParam("oosImagesId") Integer oosImagesId,
//                               HttpServletResponse response) throws IOException {
//
//        // 查看学生文件（只需要oosImages_id）
//        oosImagesService.lookApplyImage(-oosImagesId, response);
//    }
}
