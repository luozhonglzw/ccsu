package cn.ccsu.cecs.bonus.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * （内含加分项类别信息）
 */
@Data
public class StuBonusVo {
    // 加分申请表id
    private Integer bonusApplyId;
    // 学生学号
    private String stuNumber;
    // 学生姓名
    private String stuName;
    // 加分项名称
    private String bonusName;
    // 加分项 实际 加的分数  (不填 后续会根据approval自动判断)
    private BigDecimal bonusScore;
    // 审批意见
    private String approvalComments;
    // 审批时间
    private Date approvalTime;
    // 审核状态
    private Integer approval;
    // 该加分项中的所有图片id
    private List<Integer> oosImagesIds;
    private Integer categoryId;
    // 加分项类别信息
    private CategoryVo categoryVo;

    // 补加 - 申请分数（bonus_apply表中的score，具体真正加分是根据approval判断）
    private BigDecimal applyScore;
    // 补加 - 申请内容 <=> 加分申请表备注
    private String remark;
    // 补加 - 提交时间
    private Date submitTime;
}
