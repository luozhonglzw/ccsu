package cn.ccsu.cecs.student.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentLoginVo {
    // 学生序号，用于查询优化
    private Integer id;

    // 学生姓名
    private String stuName;

    // 学生学号
    private String stuNumber;

    // 学生密码
    private String password;
}
