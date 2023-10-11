package cn.ccsu.cecs.bonus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 加分申请表
 * 
 * @author ccsu-cecs
 */
@Data
@TableName("bonus_apply")
public class BonusApply implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 学年id(用于查询优化)
	 */
	private Integer yearId;
	/**
	 * 类别id(用于查询优化)
	 */
	private Integer categoryId;
	/**
	 * 加分项id
	 */
	private Integer bonusId;
	/**
	 * 学生id
	 */
	private Integer stuStudentId;
	/**
	 * 成绩(把该加分项的分数，赋值给本成绩，可能会出现负数，因为有惩戒减分，学习成绩不会使用该加分项的分数，学习成绩就是直接使用导入时excel的成绩)
	 */
	private BigDecimal score;
	/**
	 * 备注(申请备注)
	 */
	private String remark;
	/**
	 * 审批状态
	 */
	private Integer approval;
	/**
	 * 审批人
	 */
	private String approvalBy;
	/**
	 * 审批时间
	 */
	private Date approvalAt;
	/**
	 * 审批意见
	 */
	private String approvalComments;
	/**
	 * 创建记录时间
	 */
	private Date createdAt;
	/**
	 * 记录创建人
	 */
	private String createdBy;
	/**
	 * 更新记录时间
	 */
	private Date updatedAt;
	/**
	 * 记录修改人
	 */
	private String updatedBy;
	/**
	 * 逻辑删除,0未删除,1已删除
	 */
	private Integer deleted;

}
