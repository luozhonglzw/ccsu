package cn.ccsu.cecs.bonus.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 综测类别表
 */
@Data
public class CategoryVo {
    // 类别id
    private Integer id;
    // 类别名称
    private String name;
    // 类别所占权重
    private BigDecimal weight;
}
