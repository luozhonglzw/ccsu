package cn.ccsu.cecs.bonus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 申请材料表
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@TableName("bonus_apply_image")
public class BonusApplyImage implements Serializable {

    private static final long serialVersionUID = -7000745136106370541L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 申请id(相同的一次申请，最多允许有三个图片.)
     */
    private Integer bonusApplyId;

    /**
     * 图片资源id
     */
    private Integer oosImagesId;

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

    public Integer getBonusApplyId() {
        return bonusApplyId;
    }

    public void setBonusApplyId(Integer bonusApplyId) {
        this.bonusApplyId = bonusApplyId;
    }

    public Integer getOosImagesId() {
        return oosImagesId;
    }

    public void setOosImagesId(Integer oosImagesId) {
        this.oosImagesId = oosImagesId;
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
        return "BonusApplyImage{" +
                "id=" + id +
                ", bonusApplyId=" + bonusApplyId +
                ", oosImagesId=" + oosImagesId +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", updatedAt=" + updatedAt +
                ", updatedBy=" + updatedBy +
                ", deleted=" + deleted +
                "}";
    }
}
