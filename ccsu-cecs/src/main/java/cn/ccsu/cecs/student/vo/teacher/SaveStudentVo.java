package cn.ccsu.cecs.student.vo.teacher;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接收前端保存Student信息Vo
 */
@Data
public class SaveStudentVo {
    /**
     * 学院id
     */
    private Integer collegeId;

    /**
     * 年级id
     */
    private Integer gradeId;

    /**
     * 专业id
     */
    private Integer professionId;

    /**
     * 班级id
     */
    private Integer classId;

    /**
     * 姓名
     */
    private String stuName;

    /**
     * 学号
     */
    private String stuNumber;

    /**
     * 密码
     */
    private transient String password;

    /**
     * 创建记录时间
     */
    private LocalDateTime createdAt;

    /**
     * 记录创建人
     */
    private String createdBy;

    /**
     * 更新记录时间
     */
    private LocalDateTime updatedAt;

    /**
     * 记录修改人
     */
    private String updatedBy;

    /**
     * 逻辑删除,0未删除,1已删除
     */
    private Integer deleted;
}
