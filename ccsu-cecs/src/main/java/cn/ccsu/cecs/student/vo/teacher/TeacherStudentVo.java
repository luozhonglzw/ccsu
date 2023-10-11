package cn.ccsu.cecs.student.vo.teacher;

import lombok.Data;

/**
 * 返回给老师端的学生信息
 */
@Data
public class TeacherStudentVo {
    // 学生id
    private Integer id;
    // 学生班级id
    private Integer classId;
    // 学生专业id
    private Integer professionId;
    // 学生年级id
    private Integer gradeId;
    // 学生学院id
    private Integer collegeId;
    // 学生班级名称
    private String className;
    // 学生专业名称
    private String professionName;
    // 学生年级名称
    private String gradeName;
    // 学生学院名称
    private String collegeName;
}
