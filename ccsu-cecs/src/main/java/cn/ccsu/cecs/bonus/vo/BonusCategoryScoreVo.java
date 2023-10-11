package cn.ccsu.cecs.bonus.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 加分类别分数Vo（汇总每个类别的总和）
 */
@Data
public class BonusCategoryScoreVo {
    // 加分类别信息
    private CategoryVo categoryVo;
    // 该类别总成绩
    private BigDecimal score;
}
