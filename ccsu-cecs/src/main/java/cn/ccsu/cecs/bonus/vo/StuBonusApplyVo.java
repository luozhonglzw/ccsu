package cn.ccsu.cecs.bonus.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class StuBonusApplyVo {
    // 学年id
    private Integer yearId;
    // 学年名称
    private String yearName;
    // 加分项信息
    private List<StuBonusVo> stuBonusVos;
}
