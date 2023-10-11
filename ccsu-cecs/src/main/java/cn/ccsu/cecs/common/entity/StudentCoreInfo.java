package cn.ccsu.cecs.common.entity;

import lombok.Data;

/**
 * 学生核心信息
 */
@Data
public class StudentCoreInfo {
    // 学年id
    private Integer yearId;
    // 学年名称
    private String yearName;
    // 学院id
    private Integer collegeId;
    // 学院名称
    private String collegeName;
    // 年级id
    private Integer gradeId;
    // 年级名称
    private String gradeName;
    // 专业id
    private Integer professionId;
    // 专业名称
    private String professionName;
    // 班级id
    private Integer classId;
    // 班级名称
    private String className;
}
