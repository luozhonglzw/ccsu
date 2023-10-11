package cn.ccsu.cecs.student.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专业信息简化表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfessionVo {
    private Integer id;
    private String professionName;
}
