package cn.ccsu.cecs.bonus.controller.teacher;

import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.service.IBonusApplyService;
import cn.ccsu.cecs.bonus.vo.teacher.BonusApplyVo;
import cn.ccsu.cecs.bonus.vo.teacher.ModifyBonusApplyVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.LocalLock;
import cn.ccsu.cecs.common.annotation.PrintTeacherInfo;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加分申请表
 *
 * @author ccsu-cecs
 */
@Slf4j
@RestController
@RequestMapping("/teacher/bonus/bonus-apply")
public class TeacherBonusApplyController {

    @Autowired
    private IBonusApplyService bonusApplyService;

    @Autowired
    RedisUtils redisUtils;

    /**
     * 根据条件查询加分申请表信息
     *
     * @param yearId       学年id
     * @param gradeId      年级id
     * @param professionId 专业id
     * @param approval     审核状态
     * @return 结果
     */
    @PrintTeacherInfo(value = "管理员根据条件查询加分申请表信息")
    @CatchException(value = "获取审批的加分申请表异常")
    @GetMapping("/bonus-apply-list")
    public R list(@RequestParam("yearId") Integer yearId,
                  @RequestParam("gradeId") Integer gradeId,
                  @RequestParam("professionId") Integer professionId,
                  @RequestParam("approval") Integer approval,
                  @RequestParam("page") String page,
                  @RequestParam("limit") String limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("yearId", yearId);
        params.put("gradeId", gradeId);
        params.put("professionId", professionId);
        params.put("approval", approval);
        params.put("page", page);
        params.put("limit", limit);

        /**
         *  key: teacher:bonus_apply:year_grade_profession_approval
         *  {
         *      [页码]_[条数] ：  xxx
         *  }
         *  这个redis的key采用hash做
         */
        // 1.先拼接redisKey
        String redisKey = RedisKeyConstant.TEACHER_BONUS_APPLY_LIST_ALL_KEY + ":" + yearId + "_" + gradeId + "_" + professionId + "_" + approval;

        // 1.查询redis
        String hashKey = page + "_" + limit;
        JsonR r = redisUtils.hashOperationGet(redisKey, hashKey, JsonR.class);

        if (r == null) {
            PageUtils data = bonusApplyService.queryPage(params);
            if (data.getList().size() == 0) {
                redisUtils.hashOperationSet(redisKey, hashKey, new JsonR());
                return R.ok().put("data", null);
            }
            redisUtils.hashOperationSet(redisKey, hashKey, new JsonR("success", 0, data));
            return R.ok().put("data", data);
        } else {
            return R.ok().put("data", r.getData());
        }
    }

    /**
     * 教师通过学生学号查询学生的所有申请表
     */
    @PrintTeacherInfo(value = "管理员通过学生学号查询学生的所有申请表")
    @CatchException(value = "查询加分申请表异常")
    @GetMapping("/student-info")
    public R listByStuNumber(@RequestParam("yearId") Integer yearId,
                             @RequestParam("stuNumber") String stuNumber,
                             @RequestParam("approval") Integer approval) {
        List<BonusApplyVo> bonusApplyVos = bonusApplyService.getBonusApplyByStuNumberYearId(yearId, stuNumber, approval);
        return R.ok().put("data", bonusApplyVos);
    }

    /**
     * 保存单个加分申请表信息
     *
     * @param bonusApply 需要保存的加分申请表信息
     * @param request    请求
     * @return 结果
     */
    @PrintTeacherInfo(value = "保存单个加分申请表信息")
    @LocalLock(exceptionValue = "已有加分申请表正在保存中，请稍后再试", type = TokenType.TEACHER_TOKEN)
    @CatchException(value = "保存加分申请表异常")
    @RequestMapping("/save")
    public R save(@RequestBody BonusApply bonusApply, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        bonusApply.setCreatedBy(name);
        bonusApply.setCreatedAt(new Date());
        bonusApply.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        bonusApplyService.save(bonusApply);

        return R.ok();
    }

    /**
     * 修改加分申请表信息（传递过来的参数，属于老师可以修改的）
     *
     * @param modifyBonusApplyVo 接收前端需要修改的加分申请表的表单Vo
     * @param request            request请求
     * @return 结果
     */
    @PrintTeacherInfo(value = "修改加分申请表信息")
    @CatchException(value = "修改加分申请表异常")
    @PostMapping("/update")
    public R update(@RequestBody @Validated ModifyBonusApplyVo modifyBonusApplyVo, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        // 修改加分申请表
        bonusApplyService.modifyBonusApplyWithTeacher(modifyBonusApplyVo, name);

        return R.ok("修改加分申请表成功");
    }

    /**
     * 删除加分申请表信息（逻辑删除）
     *
     * @param ids 支持批量删除，加分申请表id集合
     * @return 结果
     */
    @PrintTeacherInfo(value = "删除加分申请表信息")
    @CatchException(value = "删除加分申请表异常")
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids, HttpServletRequest request) {
        String name = JWTUtils.getPayload(request, "name");

        for (Integer id : ids) {
            bonusApplyService.deleteBonusApply(id, name);
        }

        return R.ok();
    }

    /**
     * 查询所有加分申请表信息
     *
     * @param params 请求参数。支持分页查询
     * @return 结果
     */
    @PrintTeacherInfo(value = "管理员查询所有加分申请表信息")
    @CatchException(value = "查询加分申请表异常")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = bonusApplyService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 查询单个加分申请表信息
     *
     * @param id 加分申请表id
     * @return 结果
     */
    @CatchException(value = "查询加分申请表异常")
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        BonusApplyVo bonusApplyVo = bonusApplyService.getBonusApplyVo(id);

        return R.ok().put("bonusApplyVo", bonusApplyVo);
    }

}
