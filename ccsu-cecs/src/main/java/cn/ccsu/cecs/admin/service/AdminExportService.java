package cn.ccsu.cecs.admin.service;

import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AdminExportService {
    /**
     * 导出学生成绩 - excel
     *
     * @param yearId       学年id
     * @param gradeId      年级id
     * @param professionId 专业id
     * @param map          excel行的数据
     * @param request      请求
     * @param response     相应
     */
    void exportStuScore(Integer yearId, Integer gradeId, Integer professionId, ModelMap map,
                        HttpServletRequest request, HttpServletResponse response);
}
