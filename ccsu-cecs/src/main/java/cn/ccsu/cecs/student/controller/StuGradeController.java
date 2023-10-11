package cn.ccsu.cecs.student.controller;


import cn.ccsu.cecs.student.service.IStuGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 年级表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@RestController
@RequestMapping("/student/stu-grade")
public class StuGradeController {
    @Autowired
    private IStuGradeService stuGradeService;

}
