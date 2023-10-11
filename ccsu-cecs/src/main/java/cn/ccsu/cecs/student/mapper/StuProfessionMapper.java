package cn.ccsu.cecs.student.mapper;

import cn.ccsu.cecs.student.entity.StuProfession;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 专业表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Mapper
@Repository
public interface StuProfessionMapper extends BaseMapper<StuProfession> {

}
