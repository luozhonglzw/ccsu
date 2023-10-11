package cn.ccsu.cecs.bonus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 加分项表
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Data
@TableName("bonus_bonus")
public class BonusBonus implements Serializable {

    private static final long serialVersionUID = -1992316993347724500L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 类别id
     */
    @NotNull(message = "加分项类别id必须传递")
    private Integer categoryId;

    /**
     * 加分项名称(惩戒减分项目，也是这个字段)
     */
    @NotEmpty(message = "加分项名称不能为空")
    private String name;

    /**
     * 该加分项，单次所加分数(由于有惩戒减分，所以会有负数加分项)
     */
    @NotNull(message = "加分项分数必须传递")
    private BigDecimal score;

    /**
     * 该加分项的最多申请次数
     */
    @NotNull(message = "加分项最多申请次数必须传递")
    private Integer maxTimes;

    /**
     * 说明
     */
    @NotEmpty(message = "加分项说明必须传递")
    private String illustrate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建记录时间
     */
    private LocalDateTime createdAt;

    /**
     * 记录创建人
     */
    private String createdBy;

    /**
     * 更新记录时间
     */
    private LocalDateTime updatedAt;

    /**
     * 记录修改人
     */
    private String updatedBy;

    /**
     * 逻辑删除,0未删除,1已删除
     */
    private Integer deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public Integer getMaxTimes() {
        return maxTimes;
    }

    public void setMaxTimes(Integer maxTimes) {
        this.maxTimes = maxTimes;
    }

    public String getIllustrate() {
        return illustrate;
    }

    public void setIllustrate(String illustrate) {
        this.illustrate = illustrate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "BonusBonus{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", name=" + name +
                ", score=" + score +
                ", maxTimes=" + maxTimes +
                ", illustrate=" + illustrate +
                ", remark=" + remark +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", updatedAt=" + updatedAt +
                ", updatedBy=" + updatedBy +
                ", deleted=" + deleted +
                "}";
    }
}
