package cn.ccsu.cecs.admin.controller;

import cn.ccsu.cecs.admin.service.AdminImportService;
import cn.ccsu.cecs.admin.vo.ImportStuScoreVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@RequestMapping("/teacher/import")
public class AdminImportController {

    @Autowired
    AdminImportService adminImportService;

    /**
     * 批量导入学生信息
     *
     * @param file 文件
     * @return 结果
     */
    @CatchException(value = "导入学生模板信息失败")
    @PostMapping("/student-info")
    public R importStudent(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {

        // 导入学生信息
        adminImportService.importStudentInfo(file, request);
        return R.ok("导入学生信息成功");
    }


    /**
     * 批量导入学生成绩
     *
     * @param yearId     学年id
     * @param categoryId 类别id
     * @param file       文件
     * @param request    请求
     * @return 结果
     */
    @CatchException(value = "导入学生成绩信息失败")
    @PostMapping("/student-score")
    public R importAchieve(@RequestParam("yearId") Integer yearId,
                           @RequestParam("categoryId") Integer categoryId,
                           @RequestParam("file") MultipartFile file,
                           HttpServletRequest request) throws Exception {
        ImportStuScoreVo importStuScoreVo = new ImportStuScoreVo(yearId, categoryId, file);

        if (Objects.equals(importStuScoreVo.getCategoryId(), BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode())) {
            adminImportService.importStudentBaseScore(importStuScoreVo, request);
            return R.ok();
        }
        // 导入学生成绩
        adminImportService.importStudentScore(importStuScoreVo, request);
        return R.ok();
    }

}
