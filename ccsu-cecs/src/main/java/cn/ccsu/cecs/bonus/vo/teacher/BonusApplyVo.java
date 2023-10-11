package cn.ccsu.cecs.bonus.vo.teacher;

import cn.ccsu.cecs.bonus.vo.CategoryVo;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.student.vo.StuStudentVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 加分申请表
 */
@Data
public class BonusApplyVo {
    // 加分申请表id
    private Integer id;
    // 学年信息
    private YearVo yearVo;
    // 类别信息
    private CategoryVo categoryVo;
    // 加分项信息
    private String bonusName;
    // 学生信息
    private StuStudentVo studentVo;
    // 该申请表加的分（直接就是bonus_apply中的score）
    private BigDecimal bonusScore;
    // 该申请表申请加的分数
    private BigDecimal applyScore;
    // 加分申请表备注
    private String remark;
    // 审核状态
    private Integer approval;
    // 审核人
    private String approvalBy;
    // 审核时间
    private Date approvalAt;
    // 审批意见
    private String approvalComments;
    // 图片信息
    private List<Integer> oosImagesIds;
    // 创建时间
    private Date createAt;
}
