package cn.ccsu.cecs.oos.controller.teacher;

import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.oos.entity.OosImages;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 图片资源表
 *
 * @author ccsu-cecs
 */
@RestController
@RequestMapping("/teacher/oos/oos-images")
public class TeacherOosImagesController {
    @Autowired
    private IOosImagesService oosImagesService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = oosImagesService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        OosImages oosImages = oosImagesService.getById(id);

        return R.ok().put("oosImages", oosImages);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OosImages oosImages) {
        oosImagesService.save(oosImages);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OosImages oosImages) {
        oosImagesService.updateById(oosImages);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids) {
        oosImagesService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
