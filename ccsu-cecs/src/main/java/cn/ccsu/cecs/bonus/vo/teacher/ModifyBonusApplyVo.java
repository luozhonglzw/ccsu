package cn.ccsu.cecs.bonus.vo.teacher;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 接收修改加分申请表的表单Vo
 * —— 老师端
 */
@Data
public class ModifyBonusApplyVo {
    // 加分申请表id
    @NotNull(message = "申请表id必须传递")
    private String bonusApplyId;
    // 学年id
    @NotNull(message = "学年id必须传递")
    private String yearId;
    // 类别id
    @NotNull(message = "类别id必须传递")
    private String categoryId;
    // 加分项id
    @NotNull(message = "加分项id必须传递")
    private String bonusId;
    // 加分项的名称
    private String bonusName;
    // 加分的分数，还没乘以权重
    @NotNull(message = "加分分数必须传递")
    private String score;
    // 加分申请表的备注
    private String remark;
}
