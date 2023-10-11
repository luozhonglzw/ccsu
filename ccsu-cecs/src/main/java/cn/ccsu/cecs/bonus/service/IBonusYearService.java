package cn.ccsu.cecs.bonus.service;

import cn.ccsu.cecs.bonus.entity.BonusYear;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学年表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IBonusYearService extends IService<BonusYear> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取所有学年的信息
     *
     * @return
     */
    List<YearVo> getAllYear();

    /**
     * 查询学年信息
     *
     * @param id 学年id
     * @return 学年信息
     */
    YearVo getYearVo(Integer id);
}
