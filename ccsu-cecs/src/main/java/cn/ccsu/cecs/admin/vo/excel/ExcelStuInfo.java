package cn.ccsu.cecs.admin.vo.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import lombok.Data;

/**
 * 导入学生信息Vo
 */
@Data
public class ExcelStuInfo {
    @Excel(name = "学号")
    private String stuNumber;

    @Excel(name = "年级")
    private String gradeName;

    @Excel(name = "姓名")
    private String stuName;

    @Excel(name = "学院")
    private String collegeName;

    @Excel(name = "专业")
    private String professionName;

    @Excel(name = "班级")
    private String className;

    // 用于写入数据库
    private Integer collegeId;
    private Integer gradeId;
    private Integer professionId;
    private Integer classId;
    private String password = ProjectConstant.STUDENT_DEFAULT_PASSWORD;
}
