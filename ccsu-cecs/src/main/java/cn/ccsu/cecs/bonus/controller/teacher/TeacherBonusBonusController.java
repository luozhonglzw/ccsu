package cn.ccsu.cecs.bonus.controller.teacher;

import cn.ccsu.cecs.bonus.entity.BonusBonus;
import cn.ccsu.cecs.bonus.service.IBonusBonusService;
import cn.ccsu.cecs.bonus.vo.teacher.BonusBonusVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.PrintTeacherInfo;
import cn.ccsu.cecs.common.annotation.RedisCache;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加分项表
 *
 * @author ccsu-cecs
 */
@Slf4j
@RestController
@RequestMapping("/teacher/bonus/bonus-bonus")
public class TeacherBonusBonusController {

    @Autowired
    private IBonusBonusService bonusBonusService;

    @Autowired
    RedisUtils redisUtils;

    @PrintTeacherInfo(value = "管理员查询加分项信息")
    @CatchException(value = "查询加分项表异常")
    @GetMapping("/search")
    public R search(@RequestParam("name") String bonusName,
                    @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        List<BonusBonusVo> bonusBonusVos;
        if (bonusName.equals("undefined")) {
            bonusName = "";
        }
        if (categoryId == null) {
            bonusBonusVos = bonusBonusService.searchByName(bonusName);
        } else {
            // 根据名称/类别id 查询加分项信息
            bonusBonusVos = bonusBonusService.searchByNameAndCategoryId(bonusName, categoryId);
        }

        log.info("管理员信息：" + TeacherInterceptor.teacherInfoThreadLocal.get() + " -> 查询加分项信息");
        TeacherInterceptor.teacherInfoThreadLocal.remove();
        return R.ok().put("data", bonusBonusVos);
    }

    /**
     * 查询所有加分项信息
     *
     * @return 结果
     */
    // TODO 这里修改了返回字段名  page、limit修改为必填
    @PrintTeacherInfo(value = "管理员查询所有加分项信息")
    @CatchException(value = "查询加分项表异常")
    @RequestMapping("/list")
    public R list(@RequestParam(value = "page") String page,
                  @RequestParam(value = "limit") String limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("limit", limit);

        /**
         *  key:  teacher:bonus:name:list
         *  {
         *      [页码]_[条数]   xxx
         *  }
         *  这个redis的key采用hash做
         */
        // 1.先拼接redisKey
        String redisKey = RedisKeyConstant.TEACHER_BONUS_NAME_LIST_KEY;

        // 1.查询redis
        String hashKey = page + "_" + limit;
        JsonR r = redisUtils.hashOperationGet(redisKey, hashKey, JsonR.class);
        if (r == null) {
            PageUtils pageUtils = bonusBonusService.queryPage(params);
            List<BonusBonusVo> bonusBonusVos = (List<BonusBonusVo>) pageUtils.getList();
            if (bonusBonusVos == null || bonusBonusVos.size() == 0) {
                redisUtils.hashOperationSet(redisKey, hashKey, new JsonR());
                return R.ok().put("data", null);
            }
            redisUtils.hashOperationSet(redisKey, hashKey, new JsonR("success", 0, pageUtils));
            return R.ok().put("data", pageUtils);
        } else {
            return R.ok().put("data", r.getData());
        }
    }

    /**
     * 查询单个加分项信息
     *
     * @param id 加分项id
     * @return 结果
     */
    @CatchException(value = "查询加分项表异常")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        BonusBonusVo bonusBonusVo = bonusBonusService.getBonusBonusVo(id);

        return R.ok().put("bonusBonusVo", bonusBonusVo);
    }

    /**
     * 保存单个加分项信息
     *
     * @param bonusBonus 需要保存的加分项信息
     * @return 结果
     */
    @PrintTeacherInfo(value = "管理员保存单个加分项信息")
    @CatchException(value = "保存加分项表异常")
    @RequestMapping("/save")
    public R save(@RequestBody @Validated BonusBonus bonusBonus, HttpServletRequest request) {
        BonusBonus bonus = bonusBonusService.getOne(new QueryWrapper<BonusBonus>()
                .eq("category_id", bonusBonus.getCategoryId())
                .eq("name", bonusBonus.getName())
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        if (bonus != null) {
            return R.error("该加分项信息已存在");
        }

        String name = JWTUtils.getPayload(request, "name");

        bonusBonus.setCreatedBy(name);
        bonusBonus.setCreatedAt(LocalDateTime.now());
        bonusBonus.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());

        // 先保存，再删redis
        try {
            bonusBonusService.save(bonusBonus);
            log.info("保存加分项信息：username:{}, bonusBonus:{}", name, bonusBonus);

            // 删除老师端加分项的信息
            redisUtils.delete(RedisKeyConstant.TEACHER_BONUS_NAME_LIST_KEY);
            // 删除学生端加分项的信息
            redisUtils.deleteSpecialPrefix(RedisKeyConstant.STUDENT_BONUS_INFO_ONE_SINGLE_KEY + ":" + bonusBonus.getCategoryId());
            redisUtils.delete(RedisKeyConstant.STUDENT_BONUS_NAME_LIST_SINGLE_KEY + ":" + bonusBonus.getCategoryId());
        } catch (Exception e) {
            log.error("加分项信息保存失败 bonusName:" + bonusBonus.getName());
        }
        return R.ok();
    }

    /**
     * 修改加分项信息
     *
     * @param bonusBonus 需要修改的加分项信息
     * @return 结果
     */
    @PrintTeacherInfo(value = "管理员修改加分项信息")
    @CatchException(value = "修改加分项表异常")
    @RequestMapping("/update")
    public R update(@RequestBody @Validated BonusBonus bonusBonus, HttpServletRequest request) {

        String name = JWTUtils.getPayload(request, "name");

        bonusBonus.setUpdatedAt(LocalDateTime.now());
        bonusBonus.setUpdatedBy(name);
        bonusBonus.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());

        try {
            bonusBonusService.updateById(bonusBonus);
            log.info("修改加分项信息：username:{}, bonusBonus:{}", name, bonusBonus);

            // 删除老师端加分项的信息
            redisUtils.delete(RedisKeyConstant.TEACHER_BONUS_NAME_LIST_KEY);
            // 删除学生端加分项的信息
            redisUtils.deleteSpecialPrefix(RedisKeyConstant.STUDENT_BONUS_INFO_ONE_SINGLE_KEY + ":" + bonusBonus.getCategoryId());
            redisUtils.delete(RedisKeyConstant.STUDENT_BONUS_NAME_LIST_SINGLE_KEY + ":" + bonusBonus.getCategoryId());
            // 删除aop的getById的信息
            redisUtils.delete("aop:getById:BonusBonusServiceImpl:" + bonusBonus.getId());
        } catch (Exception e) {
            log.error("加分项信息更新失败 bonusId:" + bonusBonus.getId());
        }
        return R.ok();
    }

    /**
     * 删除加分项信息（逻辑删除）
     *
     * @param bonusId 加分项id
     * @return 结果
     */
//    @CatchException(value = "删除加分项表异常")
//    @RequestMapping("/delete")
//    public R delete(@RequestParam("bonusId") Integer bonusId, HttpServletRequest request) {
//        String name = JWTUtils.getPayload(request, "name");
//
//        bonusBonusService.update(new UpdateWrapper<BonusBonus>()
//                .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
//                .set("updated_at", LocalDateTime.now())
//                .set("updated_by", name)
//                .eq("id", bonusId));
//
//        return R.ok("删除成功");
//    }


    /**
     * 根据加分类别来获取所有可以的加分名称
     *
     * @param categoryId 类别id
     * @return 加分名称
     */
    @RedisCache(key = RedisKeyConstant.STUDENT_BONUS_NAME_LIST_SINGLE_KEY)
    @CatchException(value = "获取加分项名称异常")
    @GetMapping("/getBonusName")
    public R getBonusName(@RequestParam("categoryId") Integer categoryId) {
        // 获取指定加分类别的所有加分项名称
        List<String> bonusNames = bonusBonusService.getBonusName(categoryId);
        return R.ok().put("data", bonusNames);
    }

    /**
     * 根据类别id、加分项名称获取加分项的信息
     *
     * @param categoryId 类别id
     * @param bonusName  加分项名称
     * @return 结果
     */
    @CatchException(value = "获取加分项信息异常")
    @GetMapping("/getBonusInfo")
    public R getBonusInfo(@RequestParam("categoryId") Integer categoryId,
                          @RequestParam("name") String bonusName) {

        // 根据类别id、加分项名称获取加分项的信息
        BonusBonusVo bonusBonusVos = bonusBonusService.getBonusInfo(categoryId, bonusName);
        return R.ok().put("data", bonusBonusVos);
    }

}
