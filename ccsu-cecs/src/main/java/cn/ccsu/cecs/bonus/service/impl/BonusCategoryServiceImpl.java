package cn.ccsu.cecs.bonus.service.impl;

import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.mapper.BonusCategoryMapper;
import cn.ccsu.cecs.bonus.service.IBonusCategoryService;
import cn.ccsu.cecs.bonus.vo.CategoryVo;
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
 * 加分项类别表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class BonusCategoryServiceImpl extends ServiceImpl<BonusCategoryMapper, BonusCategory> implements IBonusCategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BonusCategory> page = this.page(
                new Query<BonusCategory>().getPage(params),
                new QueryWrapper<BonusCategory>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryVo> queryCategoryVos(Map<String, Object> params) {
        QueryWrapper<BonusCategory> wrapper = new QueryWrapper<>();

        // 只查未删除的综测类别
        wrapper.eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());

        IPage<BonusCategory> page = this.page(
                new Query<BonusCategory>().getPage(params), wrapper);
        List<BonusCategory> records = page.getRecords();

        // 收集bonusCategory映射到CategoryVo
        List<CategoryVo> categoryVoList = records.stream().map((bonusCategory) -> {
            CategoryVo categoryVo = new CategoryVo();
            categoryVo.setId(bonusCategory.getId());
            categoryVo.setName(bonusCategory.getName());
            categoryVo.setWeight(bonusCategory.getWeights());
            return categoryVo;
        }).collect(Collectors.toList());

        return categoryVoList;
    }
}
