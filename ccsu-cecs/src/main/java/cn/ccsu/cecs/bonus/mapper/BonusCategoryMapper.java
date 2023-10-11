package cn.ccsu.cecs.bonus.mapper;

import cn.ccsu.cecs.bonus.entity.BonusCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 加分项类别表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Mapper
@Repository
public interface BonusCategoryMapper extends BaseMapper<BonusCategory> {

}
