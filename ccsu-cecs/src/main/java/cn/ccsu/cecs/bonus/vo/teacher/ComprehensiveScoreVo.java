package cn.ccsu.cecs.bonus.vo.teacher;

import cn.ccsu.cecs.common.entity.StudentCoreInfo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComprehensiveScoreVo {
    // 学生核心信息
    private StudentCoreInfo studentCoreInfo;
    // 学生姓名
    private String stuName;
    // 学生学号
    private String stuNumber;
    // 总成绩
    private BigDecimal score;
}
