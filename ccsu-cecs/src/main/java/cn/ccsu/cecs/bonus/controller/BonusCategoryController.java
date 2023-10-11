package cn.ccsu.cecs.bonus.controller;


import cn.ccsu.cecs.bonus.vo.CategoryVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.utils.R;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 加分项类别表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@RestController
@RequestMapping("/student/bonus/bonus-category")
public class BonusCategoryController {

    @Autowired
    DefaultCache defaultCache;

    /**
     * 查看所有综测类别
     */
    @CatchException(value = "查看综测类别失败")
    @GetMapping("/list")
    public R list() {
        List<CategoryVo> categoryVos = defaultCache.getCategoryVoCache();

        return R.ok().put("data", categoryVos);
    }


    /**
     * 查询单个综测类别的权重
     */
    @CatchException(value = "查看综测类别失败")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        CategoryVo categoryVo = defaultCache.getCategoryVo(id);

        return R.ok().put("data", categoryVo);
    }
}
