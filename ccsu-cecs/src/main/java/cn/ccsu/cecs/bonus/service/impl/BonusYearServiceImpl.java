package cn.ccsu.cecs.bonus.service.impl;

import cn.ccsu.cecs.bonus.entity.BonusYear;
import cn.ccsu.cecs.bonus.mapper.BonusYearMapper;
import cn.ccsu.cecs.bonus.service.IBonusYearService;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 学年表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class BonusYearServiceImpl extends ServiceImpl<BonusYearMapper, BonusYear> implements IBonusYearService {

    @Autowired
    DefaultCache defaultCache;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BonusYear> page = this.page(
                new Query<BonusYear>().getPage(params),
                new QueryWrapper<BonusYear>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );

        return new PageUtils(page);
    }

    @Override
    public List<YearVo> getAllYear() {
        List<BonusYear> bonusYearList = list(new QueryWrapper<BonusYear>()
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        return bonusYearList.stream().map(bonusYear -> {
            YearVo yearVo = new YearVo();
            yearVo.setYearId(bonusYear.getId());
            yearVo.setYearName(bonusYear.getName());
            return yearVo;
        }).collect(Collectors.toList());
    }

    @Override
    public YearVo getYearVo(Integer id) {

        return defaultCache.getYearVo(id);
//        BonusYear bonusYear = this.getOne(new QueryWrapper<BonusYear>()
//                .eq("id", id).eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
//        if (bonusYear != null) {
//            YearVo yearVo = new YearVo();
//            yearVo.setYearId(id);
//            yearVo.setYearName(bonusYear.getName());
//            return yearVo;
//        }
//        return null;
    }

}
