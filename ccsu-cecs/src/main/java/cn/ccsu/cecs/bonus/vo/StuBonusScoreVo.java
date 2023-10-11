package cn.ccsu.cecs.bonus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 学生成绩Vo（内含加分项所有信息）
 */
@Data
public class StuBonusScoreVo {
    // 学生学号
    private String stuNumber;
    // 学生姓名
    private String stuName;
    // 学生总成绩
    private BigDecimal totalScore;
    // 学年
    private String yearName;
    // 所在学院
    private String collegeName;
    // 所在专业
    private String professionName;
    // 所在年级
    private String gradeName;
    // 所在班级
    private String className;

    // 学生加分项
    private List<StuBonusVo> stuBonusVos;

    // 补充 - 加分类别成绩Vo
    private List<BonusCategoryScoreVo> bonusCategoryScoreVos;
    // 补充 - 学生在专业的排名
    private Integer rank;
}
