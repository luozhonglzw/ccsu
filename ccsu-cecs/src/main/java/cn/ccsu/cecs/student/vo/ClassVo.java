package cn.ccsu.cecs.student.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 班级信息简化表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassVo {
    private Integer id;
    private String className;
}
