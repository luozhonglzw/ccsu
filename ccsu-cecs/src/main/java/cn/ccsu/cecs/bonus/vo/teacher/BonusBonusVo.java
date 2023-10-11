package cn.ccsu.cecs.bonus.vo.teacher;

import cn.ccsu.cecs.bonus.vo.CategoryVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 加分项表
 */
@Data
public class BonusBonusVo {
    // 加分项id
    private Integer id;
    // 加分项名称
    private String bonusName;
    // 加分的分数
    private BigDecimal score;
    // 该加分项最多加分次数
    private Integer maxTimes;
    // 加分说明
    private String illustrate;
    // 加分备注
    private String remark;
    // 类别信息
    private CategoryVo categoryVo;
}
