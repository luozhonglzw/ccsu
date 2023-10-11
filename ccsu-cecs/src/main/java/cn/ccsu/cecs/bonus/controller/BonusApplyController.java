package cn.ccsu.cecs.bonus.controller;

import cn.ccsu.cecs.admin.utils.AdminUtils;
import cn.ccsu.cecs.bonus.service.IBonusApplyService;
import cn.ccsu.cecs.bonus.vo.ReceiveBonusApplyVo;
import cn.ccsu.cecs.bonus.vo.StuBonusApplyVo;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.bonus.vo.teacher.BonusApplyVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.LocalLock;
import cn.ccsu.cecs.common.annotation.RedisCache;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.common.utils.RedisUtils;
import cn.ccsu.cecs.config.DeadlineProperties;
import cn.ccsu.cecs.oos.entity.OosImages;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 加分申请表
 *
 * @author ccsu-cecs
 */
@Slf4j
@RestController
@RequestMapping("/student/bonus/bonus-apply")
public class BonusApplyController {
    @Autowired
    private IBonusApplyService bonusApplyService;

    @Autowired
    IOosImagesService oosImagesService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    DefaultCache defaultCache;

    @Autowired
    DeadlineProperties deadlineProperties;

    @Autowired
    TeacherInterceptor teacherInterceptor;

    /**
     * 已转至文件服务器管理
     */
//    /**
//     * 学生下载申请文件
//     *
//     * @param response 流，下载图片
//     * @throws IOException 文件IO异常
//     */
//    @CatchException(value = "下载文件失败")
//    @GetMapping("/download-apply")
//    public void downloadFile(@RequestParam("oosImagesId") Integer oosImagesId,
//                             HttpServletResponse response) throws IOException {
//
//        // 下载学生文件（只需要oosImages_id）
//        oosImagesService.downloadApply(oosImagesId, response);
//    }
//
//    /**
//     * 用户查看申请文件
//     *
//     * @param oosImagesId 图片id
//     * @param response    流，下载图片
//     * @throws IOException 文件IO异常
//     */
//    @GetMapping("/look-apply-image")
//    public void lookApplyImage(@RequestParam("oosImagesId") Integer oosImagesId,
//                               HttpServletRequest request,
//                               HttpServletResponse response) throws IOException {
//
//        // 查看学生文件（只需要oosImages_id）
//        oosImagesService.lookApplyImage(oosImagesId, response);
//    }

    /**
     * 学生删除加分申请表
     *
     * @param bonusApplyId 加分申请表id
     * @return 结果
     */
    @CatchException(value = "删除加分申请表失败")
    @PostMapping("/delete-apply")
    public R deleteBonusApply(@RequestParam("bonusApplyId") Integer bonusApplyId, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的信息
        String stuName = JWTUtils.getPayload(request, "stuName");
        // 删除申请表
        bonusApplyService.deleteBonusApply(bonusApplyId, stuName);
        return R.ok("删除成功");
    }

    /**
     * 查看用户的所有加分表信息
     *
     * @param yearId 学年id
     * @return 结果
     */
    @CatchException(value = "查询加分信息失败")
    @GetMapping("/info")
    public R list(@RequestParam("yearId") Integer yearId, HttpServletRequest request) {

        String id = JWTUtils.getPayload(request, "id");

        // 查询redis
        String redisKey = RedisKeyConstant.STUDENT_BONUS_APPLY_LIST_SINGLE_KEY + ":" + id;
        StuBonusApplyVo redisStuBonusApplyVo = redisUtils.hashOperationGet(redisKey, yearId.toString(), StuBonusApplyVo.class);
        if (redisStuBonusApplyVo == null) {
            // 查询本地
            StuBonusApplyVo stuBonusApplyVo = bonusApplyService.getStuBonusApplyVoByYearId(yearId, Integer.parseInt(id));
            if (stuBonusApplyVo != null) {
                redisUtils.hashOperationSet(redisKey, yearId.toString(), stuBonusApplyVo);
                return R.ok().put("data", stuBonusApplyVo);
            }
            return R.ok().put("data", null);
        }

        return R.ok().put("data", redisStuBonusApplyVo);
    }

    /**
     * 查看用户的所有加分表信息（所有学年的）
     *
     * @return 结果
     */
    @CatchException(value = "查询加分信息失败")
    @GetMapping("/list")
    public R list(HttpServletRequest request) {
        String id = JWTUtils.getPayload(request, "id");
        List<Integer> yearIdsCache = defaultCache.getYearVoCache().stream().map(YearVo::getYearId).collect(Collectors.toList());

        /**
         *  key: stu:bonus_apply:list:[学生id]
         *  {
         *      [学年id]:[加分申请表信息]
         *  }
         *  这个redis的key采用hash做
         */
        String redisKey = RedisKeyConstant.STUDENT_BONUS_APPLY_LIST_SINGLE_KEY + ":" + id;
        List<StuBonusApplyVo> rList = redisUtils.hashOperationMultiGet(redisKey, StuBonusApplyVo.class);
        List<Integer> newCacheYearIds = rList.stream().map(StuBonusApplyVo::getYearId).collect(Collectors.toList());

        if (rList.size() == 0) {
            // 获取用户的所有加分表信息
            List<StuBonusApplyVo> stuBonusApplyVos = bonusApplyService.getAllStuBonusApplyVo(Integer.parseInt(id));
            if (stuBonusApplyVos != null && stuBonusApplyVos.size() > 0) {
                // 保存至缓存
                for (StuBonusApplyVo stuBonusApplyVo : stuBonusApplyVos) {
                    Integer hashKey = stuBonusApplyVo.getYearId();
                    redisUtils.hashOperationSet(redisKey, hashKey.toString(), stuBonusApplyVo);
                }
                return R.ok().put("data", stuBonusApplyVos);
            }
            return R.ok().put("data", null);
        }

        // 判断学年信息是否完整
        if (newCacheYearIds.size() == yearIdsCache.size()) {
            return R.ok().put("data", rList);
        }

        // 判断哪个学年的申请表被删除了
        List<Integer> deletedYearIds = AdminUtils.getDiff(yearIdsCache, newCacheYearIds);
        for (Integer deletedYearId : deletedYearIds) {
            StuBonusApplyVo stuBonusApplyVo = bonusApplyService.getStuBonusApplyVoByYearId(deletedYearId, Integer.parseInt(id));
            // 保存至redis
            redisUtils.hashOperationSet(redisKey, deletedYearId.toString(), stuBonusApplyVo);
            rList.add(stuBonusApplyVo);
        }

        return R.ok().put("data", rList);
    }

    /**
     * 查看用户的单个加分申请表的信息
     *
     * @param bonusApplyId 加分申请表id
     * @return 结果
     */
    @RedisCache(key = RedisKeyConstant.STUDENT_BONUS_APPLY_ONE_SINGLE_KEY)
    @CatchException(value = "查询加分信息失败")
    @GetMapping("/info/{bonusApplyId}")
    public R info(@PathVariable("bonusApplyId") Integer bonusApplyId) {

        // 获取用户的单个加分表信息
        BonusApplyVo bonusApplyVo = bonusApplyService.getBonusApplyVoByBonusApplyId(bonusApplyId);
        return R.ok().put("data", bonusApplyVo);
    }

    /**
     * 处理用户提交的加分申请表
     *
     * @return 响应结果
     */
    @LocalLock(exceptionValue = "已有申请正在提交，请稍后再试", type = TokenType.STUDENT_TOKEN)
    @CatchException(value = "提交加分申请文件失败")
    @PostMapping("/submit-apply")
    public R addApplyFile(@RequestParam("yearId") Integer yearId,
                          @RequestParam("categoryId") Integer categoryId,
                          @RequestParam("bonusName") String bonusName,
                          @RequestParam("score") String bonusScore,
                          @RequestParam("remark") String remark,
                          @RequestParam(value = "file", required = false) List<MultipartFile> files,
                          HttpServletRequest request) {
        // 过滤ip，临时设置的截止时间
        try {
            teacherInterceptor.filterIp(request);
        } catch (Exception e) {
            String time = deadlineProperties.getTime();
            try {
                Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
                if (new Date().getTime() > parse.getTime()) {
                    throw new RuntimeException(time + " 之后已无法提交申请表");
                }
            } catch (ParseException a) {
                log.error("日期解析失败");
                throw new RuntimeException("系统异常，无法提交申请表");
            }
        }

        // 数据校验，防止
        if (!Objects.equals(categoryId, BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode())) {
            return R.error("无法对其他类别进行加分处理");
        }

        BigDecimal score;
        try {
            score = new BigDecimal(bonusScore);
        } catch (NumberFormatException e) {
            throw new RuntimeException("申请分数必须为数值型");
        }

        // 通过jwt拿到request的用户信息
        StudentLoginVo user = getUserInfo(request);

        ReceiveBonusApplyVo receiveBonusApplyVo = new ReceiveBonusApplyVo();
        receiveBonusApplyVo.setYearId(yearId);
        receiveBonusApplyVo.setCategoryId(categoryId);
        receiveBonusApplyVo.setStuStudentId(user.getId());
        receiveBonusApplyVo.setBonusName(bonusName);
        receiveBonusApplyVo.setBonusScore(score);
        receiveBonusApplyVo.setRemark(remark);
        receiveBonusApplyVo.setApplyImages(files);

        // 提交加分申请表
        bonusApplyService.submitApplyInfo(user, receiveBonusApplyVo);

        return R.ok("上传文件成功");
    }

    /**
     * 获取学生信息
     *
     * @param request request请求
     * @return 学生信息
     */
    @NotNull
    private StudentLoginVo getUserInfo(HttpServletRequest request) {
        String id = JWTUtils.getPayload(request, "id");
        String stuNumber = JWTUtils.getPayload(request, "stuNumber");
        String stuName = JWTUtils.getPayload(request, "stuName");
        return new StudentLoginVo(Integer.parseInt(id), stuName, stuNumber, null);
    }
}
