package cn.ccsu.cecs.bonus.service;

import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.vo.CategoryVo;
import cn.ccsu.cecs.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 加分项类别表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IBonusCategoryService extends IService<BonusCategory> {
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询综测类别信息
     *
     * @param params 查询参数
     * @return 综测类别信息
     */
    List<CategoryVo> queryCategoryVos(Map<String, Object> params);
}
