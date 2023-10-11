package cn.ccsu.cecs.bonus.vo.teacher;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 审核对象
 */
@Data
public class VerifyVo {
    // 加分申请表id
    private Integer bonusApplyId;
    // 审核状态
    private Integer approval;
    // 审核意见
    private String approvalComments;
    // 审核分数
    private BigDecimal bonusScore;
}
