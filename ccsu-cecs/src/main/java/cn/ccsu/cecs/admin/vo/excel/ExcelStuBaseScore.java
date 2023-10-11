package cn.ccsu.cecs.admin.vo.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 导入学生基础成绩（专业成绩）
 */
@Data
public class ExcelStuBaseScore {
    @Excel(name = "学号")
    private String stuNumber;

    // 加分分数
    @Excel(name = "专业成绩")
    private BigDecimal baseScore;
}
