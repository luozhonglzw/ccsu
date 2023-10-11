package cn.ccsu.cecs.bonus.mapper;

import cn.ccsu.cecs.bonus.entity.BonusApplyImage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 申请材料表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Mapper
@Repository
public interface BonusApplyImageMapper extends BaseMapper<BonusApplyImage> {

    List<Integer> queryOosImagesIds(@Param("bonusApplyId") Integer bonusApplyId);
}
