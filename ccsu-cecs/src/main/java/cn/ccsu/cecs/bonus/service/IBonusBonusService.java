package cn.ccsu.cecs.bonus.service;

import cn.ccsu.cecs.bonus.entity.BonusBonus;
import cn.ccsu.cecs.bonus.vo.teacher.BonusBonusVo;
import cn.ccsu.cecs.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 加分项表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IBonusBonusService extends IService<BonusBonus> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询单个加分项信息
     *
     * @param id 加分项id
     * @return 加分项信息
     */
    BonusBonusVo getBonusBonusVo(Integer id);

    /**
     * 根据加分项类别获取加分项名称
     *
     * @param categoryId 加分项类别id
     * @return 加分项名称
     */
    List<String> getBonusName(Integer categoryId);

    /**
     * 根据类别id、加分项名称获取加分项的信息
     *
     * @param categoryId 类别id
     * @param bonusName  加分项名称
     * @return 结果
     */
    BonusBonusVo getBonusInfo(Integer categoryId, String bonusName);

    /**
     * 根据加分项名称查询加分项信息（模糊查询）
     *
     * @param bonusName 加分项名称
     * @return 加分项信息
     */
    List<BonusBonusVo> searchByName(String bonusName);

    /**
     * 根据加分项名称/类别id查询加分项信息（模糊查询）
     *
     * @param bonusName  加分项名称
     * @param categoryId 加分项id
     * @return 加分项信息
     */
    List<BonusBonusVo> searchByNameAndCategoryId(String bonusName, Integer categoryId);
}

