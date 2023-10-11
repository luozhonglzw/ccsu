package cn.ccsu.cecs.oos.controller;


import cn.ccsu.cecs.oos.service.IOosImagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 图片资源表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Slf4j
@RestController
@RequestMapping("/oos/oos-images")
@CrossOrigin
public class OosImagesController {

    @Autowired
    private IOosImagesService oosImagesService;

}
