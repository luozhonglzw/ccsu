package cn.ccsu.cecs.admin.service;

import cn.ccsu.cecs.admin.vo.ImportStuScoreVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface AdminImportService {

    /**
     * 批量导入学生信息
     *
     * @param file    文件
     * @param request 请求
     */
    void importStudentInfo(MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     * 批量导入学生成绩
     *
     * @param importStuScoreVo 需要导入的学生成绩Vo
     * @param request          请求
     */
    void importStudentScore(ImportStuScoreVo importStuScoreVo, HttpServletRequest request) throws Exception;

    /**
     * 批量导入学生成绩（专业成绩）
     *
     * @param importStuScoreVo 需要导入的学生成绩Vo
     * @param request          请求
     */
    void importStudentBaseScore(ImportStuScoreVo importStuScoreVo, HttpServletRequest request) throws Exception;
}
