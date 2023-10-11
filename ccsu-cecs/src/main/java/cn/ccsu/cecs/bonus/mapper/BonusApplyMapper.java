package cn.ccsu.cecs.bonus.mapper;

import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.vo.StuBonusVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 加分申请表
 * 
 * @author yjr
 * @email yjr@gmail.com
 * @date 2022-03-11 14:43:43
 */
@Mapper
@Repository
public interface BonusApplyMapper extends BaseMapper<BonusApply> {

    /**
     * 根据学生id查询学生所有加分项的信息（StuBonusVo）
     * @param studentId
     * @return
     */
    List<StuBonusVo> getStuBonusVosByStuId(@Param("stu_student_id") Integer studentId);

    /**
     * 根据学生id和学年id查询学生加分项的信息（StuBonusVo）
     * @return
     */
    List<StuBonusVo> getStuBonusVosByStuAndYearId(@Param("stu_student_id") Integer studentId, @Param("year_id") Integer yearId);

}
