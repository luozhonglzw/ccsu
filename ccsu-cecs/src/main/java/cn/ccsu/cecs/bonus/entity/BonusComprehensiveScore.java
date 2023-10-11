package cn.ccsu.cecs.bonus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 综合成绩表
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-10
 */
@TableName("bonus_comprehensive_score")
@AllArgsConstructor
@NoArgsConstructor
public class BonusComprehensiveScore implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学年id(用于查询优化)
     */
    private Integer yearId;

    /**
     * 学院id(用于查询优化)
     */
    private Integer collegeId;

    /**
     * 年级id(用于查询优化)
     */
    private Integer gradeId;

    /**
     * 专业id(用于查询优化)
     */
    private Integer professionId;

    /**
     * 班级id(用于查询优化)
     */
    private Integer classId;

    /**
     * 学生id
     */
    private Integer stuStudentId;

    /**
     * 本学年的最终综合成绩(该成绩是三大类成绩乘以权重后求和计算出来的)
     */
    private BigDecimal score;

    /**
     * 基础成绩
     */
    private BigDecimal baseScore;

    /**
     * 综测加分成绩
     */
    private BigDecimal comprehensiveScore;

    /**
     * 惩戒分数
     */
    private BigDecimal disciplineScore;

    /**
     * 实践与创新能力分数
     */
    private BigDecimal practicalInnovationScore;

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

    public Integer getYearId() {
        return yearId;
    }

    public void setYearId(Integer yearId) {
        this.yearId = yearId;
    }

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getStuStudentId() {
        return stuStudentId;
    }

    public void setStuStudentId(Integer stuStudentId) {
        this.stuStudentId = stuStudentId;
    }


    public BigDecimal getScore() {
        return score;
    }

    public void setBaseScore(BigDecimal baseScore) {
        this.baseScore = baseScore;
    }

    public BigDecimal getBaseScore() {
        return baseScore;
    }

    public void setComprehensiveScore(BigDecimal comprehensiveScore) {
        this.comprehensiveScore = comprehensiveScore;
    }

    public BigDecimal getComprehensiveScore() {
        return comprehensiveScore;
    }

    public void setPracticalInnovationScore(BigDecimal practicalInnovationScore) {
        this.practicalInnovationScore = practicalInnovationScore;
    }

    public BigDecimal getDisciplineScore() {
        return disciplineScore;
    }

    public void setDisciplineScore(BigDecimal disciplineScore) {
        this.disciplineScore = disciplineScore;
    }

    public BigDecimal getPracticalInnovationScore() {
        return practicalInnovationScore;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
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
        return "BonusComprehensiveScore{" +
                "id=" + id +
                ", yearId=" + yearId +
                ", collegeId=" + collegeId +
                ", gradeId=" + gradeId +
                ", professionId=" + professionId +
                ", classId=" + classId +
                ", stuStudentId=" + stuStudentId +
                ", score=" + score +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                ", updatedAt=" + updatedAt +
                ", updatedBy=" + updatedBy +
                ", deleted=" + deleted +
                "}";
    }
}
