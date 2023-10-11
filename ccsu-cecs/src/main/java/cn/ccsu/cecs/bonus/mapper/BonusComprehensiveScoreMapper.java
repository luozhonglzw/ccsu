package cn.ccsu.cecs.bonus.mapper;

import cn.ccsu.cecs.bonus.entity.BonusComprehensiveScore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 综合成绩表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-10
 */
@Mapper
@Repository
public interface BonusComprehensiveScoreMapper extends BaseMapper<BonusComprehensiveScore> {

}
