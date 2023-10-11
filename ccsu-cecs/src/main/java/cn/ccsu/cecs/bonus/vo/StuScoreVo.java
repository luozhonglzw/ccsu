package cn.ccsu.cecs.bonus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StuScoreVo {
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

    // 学生学号
    private String stuNumber;
    // 学生姓名
    private String stuName;
    // 学生成绩
    private BigDecimal stuScore;

    // 补充 - 排名
    private Integer rank;
    // 补充 - 加分类别成绩Vo
    private List<BonusCategoryScoreVo> bonusCategoryScoreVos;
}
