package cn.ccsu.cecs.admin.vo.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 导入学生成绩Vo
 */
@Data
public class ExcelStuScore {
    @Excel(name = "学号")
    private String stuNumber;

    @Excel(name = "备注")
    private String remark;

    // 活动名称
    @Excel(name = "活动名称")
    private String activityName;

    // 加分分数
    @Excel(name = "加分分数")
    private BigDecimal bonusScore;

}
