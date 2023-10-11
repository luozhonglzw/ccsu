package cn.ccsu.cecs.bonus.controller;


import cn.ccsu.cecs.bonus.mapper.BonusApplyMapper;
import cn.ccsu.cecs.bonus.service.IBonusYearService;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.utils.DateUtils;
import cn.ccsu.cecs.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 学年表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@RestController
@RequestMapping("/student/bonus/bonus-year")
public class BonusYearController {

    @Autowired
    BonusApplyMapper bonusApplyMapper;

    @Autowired
    DefaultCache defaultCache;


    /**
     * 获得可以申请综测的时间
     */
    @CatchException(value = "查询所有的学年信息失败")
    @RequestMapping("/getApprovalYear")
    public R getLastYear() {
        List<YearVo> yearVoCache = defaultCache.getYearVoCache();
        String lastYear = DateUtils.getLastYear();
        for (YearVo yearVo : yearVoCache) {
            if (yearVo.getYearName().equals(lastYear)) {
                List<YearVo> list = new ArrayList<>();
                list.add(yearVo);
                return R.ok().put("data", list);
            }
        }
        return R.ok().put("data", null);
    }


    /**
     * 查询所有的学年信息
     */
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
}
