package cn.ccsu.cecs.bonus.service.impl;

import cn.ccsu.cecs.bonus.entity.BonusApplyImage;
import cn.ccsu.cecs.bonus.mapper.BonusApplyImageMapper;
import cn.ccsu.cecs.bonus.service.IBonusApplyImageService;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.common.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 申请材料表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class BonusApplyImageServiceImpl extends ServiceImpl<BonusApplyImageMapper, BonusApplyImage> implements IBonusApplyImageService {

    @Autowired
    RedisUtils redisUtils;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BonusApplyImage> page = this.page(
                new Query<BonusApplyImage>().getPage(params),
                new QueryWrapper<BonusApplyImage>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据加分申请表id查询oosIds
     *
     * @param bonusApplyId 加分申请表id
     * @return oosImagesId
     */
    @Override
    public List<Integer> getOosImagesByBonusApplyId(Integer bonusApplyId) {
        String redisKey = RedisKeyConstant.STUDENT_OOS_IMAGES_BY_APPLY_ID_LIST_KEY + ":" + bonusApplyId;
        List<Integer> result = redisUtils.get(redisKey, List.class);
        if (result == null) {
            List<Integer> oosImagesIds = this.baseMapper.queryOosImagesIds(bonusApplyId);
            if (oosImagesIds != null && oosImagesIds.size() > 0) {
                redisUtils.set(redisKey, oosImagesIds);
                return oosImagesIds;
            } else {
                List<Integer> nullList = new ArrayList<>();
                redisUtils.set(redisKey, nullList, 120);
                return nullList;
            }
        } else {
            return result;
        }
    }
}
