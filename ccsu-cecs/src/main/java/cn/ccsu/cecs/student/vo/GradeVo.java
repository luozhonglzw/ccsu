package cn.ccsu.cecs.student.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 年级信息简化表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GradeVo {
    private Integer id;
    private String gradeName;
}
