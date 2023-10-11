package cn.ccsu.cecs.bonus.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * 接收学生综测申请表信息（包括申请图片）
 */
@Data
public class ReceiveBonusApplyVo {
    // 学年id（一次申请只有一个id）
    private Integer yearId;
    // 类别id（一次申请只有一个id）
    private Integer categoryId;
    // 学生id（一次申请只有一个id）
    private Integer stuStudentId;
    // 加分分数（学生传递的）
    private BigDecimal bonusScore;
    // 加分项名称
    private String bonusName;
    // 学生提交的备注
    private String remark;
    // 申请图片
    private List<MultipartFile> applyImages;
}
