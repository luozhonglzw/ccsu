package cn.ccsu.cecs.bonus.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StuScoreDetailsVo {
    // 加分申请表id
    private Integer bonusApplyId;
    // 学年id (不查db，查缓存）
    private Integer yearId;
    // 学年名称 （不查db，查缓存）
    private String yearName;
    // 学生姓名
    private String stuName;
    // 学生学号
    private String stuNumber;
    // 类别id（只是为了号查询vo）
    private Integer categoryId;
    // 类别信息 （不查db，查缓存）
    private CategoryVo categoryVo;
    // 加分分数
    private BigDecimal bonusScore;
    // 加分名称
    private String bonusName;
    // 加分备注
    private String remark;
    // 提交时间
    private Date submitTime;
    // 审核时间
    private Date approvalTime;
    // 审核意见
    private String approvalComments;
    // 总条数——用于分页
    private Long totalCount;
}
