package cn.ccsu.cecs.bonus.mapper;

import cn.ccsu.cecs.bonus.entity.BonusYear;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 学年表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Mapper
@Repository
public interface BonusYearMapper extends BaseMapper<BonusYear> {

}
