package cn.ccsu.cecs.bonus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 加分项类别表
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@TableName("bonus_category")
public class BonusCategory implements Serializable {

    private static final long serialVersionUID = 6057819773009759014L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 综测加分类别(如:学习成绩、素拓成绩、实际与创新能力成绩)
     */
    private String name;

    /**
     * 该类别所代表的权重(如:学习成绩占60%,素拓成绩占20%,实际与创新能力成绩占20%)
     */
    private BigDecimal weights;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getWeights() {
        return weights;
    }

    public void setWeights(BigDecimal weights) {
        this.weights = weights;
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
        return "BonusCategory{" +
                "id=" + id +
                ", name=" + name +
                ", weights=" + weights +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", updatedAt=" + updatedAt +
                ", updatedBy=" + updatedBy +
                ", deleted=" + deleted +
                "}";
    }
}
