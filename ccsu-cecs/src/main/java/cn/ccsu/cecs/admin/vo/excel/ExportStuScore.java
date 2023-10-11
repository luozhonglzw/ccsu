package cn.ccsu.cecs.admin.vo.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 导出学生成绩对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportStuScore {
    // 学年
    @Excel(name = "学年", width = 15.0d)
    private String yearName;
    // 姓名
    @Excel(name = "姓名", width = 13.0d)
    private String stuName;
    // 学号
    @Excel(name = "学号", width = 18.0d)
    private String stuNumber;
    // 专业
    @Excel(name = "专业", width = 15.0d)
    private String professionName;
    // 班级
    @Excel(name = "班级", width = 15.0d)
    private String className;
    // 学院
    @Excel(name = "学院", width = 15.0d)
    private String collegeName;
    // 总成绩
    @Excel(name = "总成绩", width = 15.0d)
    private BigDecimal totalScore;
    // 排名
    @Excel(name = "排名", width = 12.0d)
    private Integer rank;
    // 专业成绩（60%）分数
    @Excel(name = "专业成绩（60%）", width = 18.0d)
    private BigDecimal professionScore;
    // 实践与创新（20%）分数
    @Excel(name = "基本素质测评成绩（20%）", width = 18.0d)
    private BigDecimal basicQualityScore;
    // 综测加分（20%）分数
    @Excel(name = "实践与创新（20%）", width = 18.0d)
    private BigDecimal practiceInnovationScore;

}
