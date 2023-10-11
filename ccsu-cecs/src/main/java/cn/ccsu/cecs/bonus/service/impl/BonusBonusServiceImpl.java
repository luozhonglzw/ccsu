package cn.ccsu.cecs.bonus.service.impl;

import cn.ccsu.cecs.bonus.entity.BonusBonus;
import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.mapper.BonusBonusMapper;
import cn.ccsu.cecs.bonus.service.IBonusBonusService;
import cn.ccsu.cecs.bonus.service.IBonusCategoryService;
import cn.ccsu.cecs.bonus.vo.CategoryVo;
import cn.ccsu.cecs.bonus.vo.teacher.BonusBonusVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.event.AsyncGainFailedEvent;
import cn.ccsu.cecs.common.event.AsyncResetTimeEvent;
import cn.ccsu.cecs.common.exception.QueryTimeOutException;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import cn.ccsu.cecs.common.misc.GlobalTimeManage;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.common.utils.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 加分项表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class BonusBonusServiceImpl extends ServiceImpl<BonusBonusMapper, BonusBonus> implements IBonusBonusService {

    @Autowired
    IBonusCategoryService bonusCategoryService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    DefaultCache defaultCache;

    @Autowired
    GlobalExecutor globalExecutor;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    IBonusBonusService bonusBonusService;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public BonusBonus getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BonusBonus> page = this.page(
                new Query<BonusBonus>().getPage(params),
                new QueryWrapper<BonusBonus>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );

        // 获取原始的BonusBonus的分页数据
        List<BonusBonus> originBonusBonuses = page.getRecords();
        Page<BonusBonusVo> bonusBonusVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        // 获取BonusBonusVo对象
        List<BonusBonusVo> bonusBonusVos = originBonusBonuses.stream().map(bonusBonus -> {
            return getBonusBonusVo(bonusBonus.getId());
        }).collect(Collectors.toList());

        // 设置BonusBonusVo的分页命中的数据
        bonusBonusVoPage.setRecords(bonusBonusVos);

        return new PageUtils(bonusBonusVoPage);
    }

    /**
     * 查询单个加分项信息
     *
     * @param id 加分项id
     * @return 加分项信息
     */
    @Override
    public BonusBonusVo getBonusBonusVo(Integer id) {
        BonusBonusVo bonusBonusVo = new BonusBonusVo();

        CompletableFuture<Void> categoryFuture = CompletableFuture.supplyAsync(() -> {
            // 查询加分项表信息
            BonusBonus originBonusBonus = bonusBonusService.getById(id);
            bonusBonusVo.setId(originBonusBonus.getId());
            bonusBonusVo.setBonusName(originBonusBonus.getName());
            bonusBonusVo.setScore(originBonusBonus.getScore().setScale(2, RoundingMode.HALF_UP));
            bonusBonusVo.setMaxTimes(originBonusBonus.getMaxTimes());
            bonusBonusVo.setIllustrate(originBonusBonus.getIllustrate());
            bonusBonusVo.setRemark(originBonusBonus.getRemark());

            return originBonusBonus.getCategoryId();
        }, executor).thenAcceptAsync(categoryId -> {
            CategoryVo categoryVo = defaultCache.getCategoryVo(categoryId);

            bonusBonusVo.setCategoryVo(categoryVo);
        });

        try {
            // 阻塞等待填充完BonusBonusVo
            categoryFuture.get(GlobalTimeManage.BASE_ASYNC_TIME_INTERVAL, TimeUnit.SECONDS);

            // 如果请求正常，恢复时间
            this.applicationContext.publishEvent(new AsyncResetTimeEvent(this, ProjectConstant.BASE_ASYNC));

            return bonusBonusVo;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("查询加分项表失败");
        } catch (TimeoutException e) {
            // 如果获取超时，那就给他获取时长+1
            this.applicationContext.publishEvent(new AsyncGainFailedEvent(this, ProjectConstant.BASE_ASYNC));

            throw new QueryTimeOutException("查询加分项表超时");
        }
    }


    /**
     * 根据加分项类别获取加分项名称
     *
     * @param categoryId 加分项类别id
     * @return 加分项名称
     */
    @Override
    public List<String> getBonusName(Integer categoryId) {
        List<BonusBonus> bonusBonuses = this.list(new QueryWrapper<BonusBonus>()
                .eq("category_id", categoryId)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        return bonusBonuses.stream().map(BonusBonus::getName).collect(Collectors.toList());
    }

    /**
     * 根据类别id、加分项名称获取加分项的信息
     *
     * @param categoryId 类别id
     * @param bonusName  加分项名称
     * @return 结果
     */
    @Override
    public BonusBonusVo getBonusInfo(Integer categoryId, String bonusName) {
        BonusBonus bonusBonus = this.getOne(new QueryWrapper<BonusBonus>()
                .eq("category_id", categoryId)
                .eq("name", bonusName)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        // 查询加分类别信息
        CategoryVo categoryVo = defaultCache.getCategoryVo(categoryId);

        BonusBonusVo bonusBonusVo = new BonusBonusVo();
        bonusBonusVo.setId(bonusBonus.getId());
        bonusBonusVo.setBonusName(bonusBonus.getName());
        bonusBonusVo.setScore(bonusBonus.getScore());
        bonusBonusVo.setMaxTimes(bonusBonus.getMaxTimes());
        bonusBonusVo.setIllustrate(bonusBonus.getIllustrate());
        bonusBonusVo.setRemark(bonusBonus.getRemark());
        bonusBonusVo.setCategoryVo(categoryVo);
        return bonusBonusVo;
    }

    /**
     * 根据加分项名称查询加分项信息（模糊查询）
     *
     * @param bonusName 加分项名称
     * @return 加分项信息
     */
    @Override
    public List<BonusBonusVo> searchByName(String bonusName) {
        List<BonusBonus> bonusBonuses = list(new QueryWrapper<BonusBonus>()
                .like("name", bonusName)
                .like("illustrate", bonusName)
                .like("remark", bonusName)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        List<BonusCategory> bonusCategories = bonusCategoryService.list();

        return bonusBonuses.stream().map(bonusBonus -> {
            BonusBonusVo bonusBonusVo = new BonusBonusVo();
            bonusBonusVo.setId(bonusBonus.getId());
            bonusBonusVo.setBonusName(bonusBonus.getName());
            bonusBonusVo.setScore(bonusBonus.getScore());
            bonusBonusVo.setMaxTimes(bonusBonus.getMaxTimes());
            bonusBonusVo.setIllustrate(bonusBonus.getIllustrate());
            bonusBonusVo.setRemark(bonusBonus.getRemark());
            for (BonusCategory bonusCategory : bonusCategories) {
                if (Objects.equals(bonusBonus.getCategoryId(), bonusCategory.getId())) {
                    CategoryVo categoryVo = new CategoryVo();
                    categoryVo.setId(bonusCategory.getId());
                    categoryVo.setName(bonusCategory.getName());
                    categoryVo.setWeight(bonusCategory.getWeights());
                    bonusBonusVo.setCategoryVo(categoryVo);
                    break;
                }
            }
            return bonusBonusVo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据加分项名称/类别id查询加分项信息（模糊查询）
     *
     * @param bonusName  加分项名称
     * @param categoryId 加分项id
     * @return 加分项信息
     */
    @Override
    public List<BonusBonusVo> searchByNameAndCategoryId(String bonusName, Integer categoryId) {
        List<BonusBonus> bonusBonuses = list(new QueryWrapper<BonusBonus>()
                .like("name", bonusName)
                .eq("category_id", categoryId)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        List<BonusCategory> bonusCategories = bonusCategoryService.list();

        return bonusBonuses.stream().map(bonusBonus -> {
            BonusBonusVo bonusBonusVo = new BonusBonusVo();
            bonusBonusVo.setId(bonusBonus.getId());
            bonusBonusVo.setBonusName(bonusBonus.getName());
            bonusBonusVo.setScore(bonusBonus.getScore());
            bonusBonusVo.setMaxTimes(bonusBonus.getMaxTimes());
            bonusBonusVo.setIllustrate(bonusBonus.getIllustrate());
            bonusBonusVo.setRemark(bonusBonus.getRemark());
            for (BonusCategory bonusCategory : bonusCategories) {
                if (Objects.equals(bonusBonus.getCategoryId(), bonusCategory.getId())) {
                    CategoryVo categoryVo = new CategoryVo();
                    categoryVo.setId(bonusCategory.getId());
                    categoryVo.setName(bonusCategory.getName());
                    categoryVo.setWeight(bonusCategory.getWeights());
                    bonusBonusVo.setCategoryVo(categoryVo);
                    break;
                }
            }
            return bonusBonusVo;
        }).collect(Collectors.toList());

    }
}
