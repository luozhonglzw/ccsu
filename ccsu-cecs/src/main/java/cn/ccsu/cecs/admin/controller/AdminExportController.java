package cn.ccsu.cecs.admin.controller;

import cn.ccsu.cecs.admin.service.AdminExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/teacher/export")
public class AdminExportController {

    @Autowired
    AdminExportService adminExportService;

    @GetMapping("/student-score")
    public void exportStuScore(@RequestParam("yearId") Integer yearId,
                               @RequestParam("gradeId") Integer gradeId,
                               @RequestParam("professionId") Integer professionId,
                               ModelMap map,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        // 导出学生成绩为excel
        adminExportService.exportStuScore(yearId, gradeId, professionId, map, request, response);
    }

}
