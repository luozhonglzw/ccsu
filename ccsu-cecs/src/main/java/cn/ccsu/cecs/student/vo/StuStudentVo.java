package cn.ccsu.cecs.student.vo;

import lombok.Data;

/**
 * 返回给前端的学生信息
 */
@Data
public class StuStudentVo {
    // 学生学号
    private String stuNumber;
    // 学生姓名
    private String stuName;
    // 班级名称
    private String className;
    // 专业名称
    private String professionName;
    // 年级名称
    private String gradeName;
    // 学院名称
    private String collegeName;
}
