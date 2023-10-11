package cn.ccsu.cecs.common.constant;

import cn.ccsu.cecs.bonus.vo.StuScoreDetailsVo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放加分申请的常量
 */
public class BonusConstant {
    @AllArgsConstructor
    @Getter
    public enum BonusApplyEnum {

        APPROVED(1, "已审批"),
        NOT_APPROVED(0, "未审批"),
        REJECTED(-1, "已驳回"),
        PRE_PASS(2, "预通过");

        private int code;
        private String message;
    }

    @AllArgsConstructor
    @Getter
    public enum CategoryWeightEnum {

        // TODO 这里待优化，没做抽象，有点紊乱    目前实现的是加分制度（大一70，大二大三90）
        BASE_SCORE(1, "基础成绩", new HashMap<>() {{
            put(null, new BigDecimal("999.00"));
        }}, new BigDecimal("0.00")),

        PRACTICE_INNOVATION_SCORE(2, "实践与创新能力", new HashMap<>() {{
            put("19级", new BigDecimal("999.00"));
            put("20级", new BigDecimal("999.00"));
            put("21级", new BigDecimal("999.00"));
        }}, new BigDecimal("0.00")),

        BASIC_QUALITY_ADD_SCORE(3, "基本素质测评成绩（A1）", new HashMap<>() {{
            put(null, new BigDecimal("25.00"));
        }}, new BigDecimal("16.00")),

        BASIC_QUALITY_SUB_SCORE(4, "基本素质测评成绩（A2）", new HashMap<>() {{
            put(null, new BigDecimal("100.00"));
        }}, new BigDecimal("0.00"));

        private Integer code;
        private String name;
        private Map<String, BigDecimal> gradeAndUpperLimitMap;   // 年级与其对应的类别上限分
        //        private BigDecimal upperLimit;   // 该类别的分数上限
        private BigDecimal benchmarkScore;   // 该类别的基准分
    }

    // 权重系数
    public static final BigDecimal WEIGHT_RATIO = new BigDecimal("0.01");
}
