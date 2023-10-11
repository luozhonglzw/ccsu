package cn.ccsu.cecs.bonus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 学年表
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@TableName("bonus_year")
public class BonusYear implements Serializable {

    private static final long serialVersionUID = -4596359236877762176L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学年名称(如:2020-2021学年，这个时间一般是指2020年9月至2021年7月，我们它称作为一学年)
     */
    private String name;

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
        return "BonusYear{" +
                "id=" + id +
                ", name=" + name +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", updatedAt=" + updatedAt +
                ", updatedBy=" + updatedBy +
                ", deleted=" + deleted +
                "}";
    }
}
