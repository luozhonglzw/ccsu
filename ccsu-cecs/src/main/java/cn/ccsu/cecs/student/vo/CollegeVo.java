package cn.ccsu.cecs.student.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学院信息简化表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CollegeVo {
    private Integer id;
    private String collegeName;
}
