package cn.ccsu.cecs.bonus.service;

import cn.ccsu.cecs.bonus.entity.BonusApplyImage;
import cn.ccsu.cecs.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 申请材料表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IBonusApplyImageService extends IService<BonusApplyImage> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据加分申请表id查询oosIds
     *
     * @param bonusApplyId 加分申请表id
     * @return oosImagesId
     */
    List<Integer> getOosImagesByBonusApplyId(Integer bonusApplyId);
}
