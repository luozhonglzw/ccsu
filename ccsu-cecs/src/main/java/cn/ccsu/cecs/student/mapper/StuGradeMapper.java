package cn.ccsu.cecs.student.mapper;

import cn.ccsu.cecs.student.entity.StuGrade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 年级表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Mapper
@Repository
public interface StuGradeMapper extends BaseMapper<StuGrade> {

}
