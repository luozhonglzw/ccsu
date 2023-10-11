package cn.ccsu.cecs.oos.mapper;

import cn.ccsu.cecs.oos.entity.OosImages;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 图片资源表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Mapper
@Repository
public interface OosImagesMapper extends BaseMapper<OosImages> {

}
