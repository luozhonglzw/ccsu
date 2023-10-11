package cn.ccsu.cecs.bonus.controller;


import cn.ccsu.cecs.bonus.service.IBonusApplyImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 申请材料表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@RestController
@RequestMapping("/student/bonus/bonus-apply-image")
public class BonusApplyImageController {
    @Autowired
    private IBonusApplyImageService bonusApplyImageService;


}
