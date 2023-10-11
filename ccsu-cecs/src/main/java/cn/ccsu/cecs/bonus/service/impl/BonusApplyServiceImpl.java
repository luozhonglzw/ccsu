package cn.ccsu.cecs.bonus.service.impl;

import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.entity.BonusApplyImage;
import cn.ccsu.cecs.bonus.entity.BonusBonus;
import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.mapper.BonusApplyMapper;
import cn.ccsu.cecs.bonus.service.*;
import cn.ccsu.cecs.bonus.vo.*;
import cn.ccsu.cecs.bonus.vo.teacher.BonusApplyVo;
import cn.ccsu.cecs.bonus.vo.teacher.ModifyBonusApplyVo;
import cn.ccsu.cecs.bonus.vo.teacher.VerifyVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import cn.ccsu.cecs.common.response.ResponseResult;
import cn.ccsu.cecs.common.utils.IOUtils;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.common.utils.RedisUtils;
import cn.ccsu.cecs.config.FileServerProperties;
import cn.ccsu.cecs.oos.entity.OosImages;
import cn.ccsu.cecs.oos.entity.OosUpload;
import cn.ccsu.cecs.oos.mapper.OosImagesMapper;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.service.IStuStudentService;
import cn.ccsu.cecs.student.vo.StuStudentVo;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service("bonusApplyService")
public class BonusApplyServiceImpl extends ServiceImpl<BonusApplyMapper, BonusApply> implements IBonusApplyService {

    @Autowired
    private IOosImagesService oosImagesService;

    @Autowired
    private OosImagesMapper oosImagesMapper;

    @Autowired
    private IBonusApplyImageService bonusApplyImageService;

    @Autowired
    private IBonusApplyService bonusApplyService;

    @Autowired
    private IBonusBonusService bonusBonusService;

    @Autowired
    private IBonusComprehensiveScoreService bonusComprehensiveScoreService;

    @Autowired
    private IBonusYearService bonusYearService;

    @Autowired
    private BonusApplyMapper bonusApplyMapper;

    @Autowired
    private IBonusCategoryService categoryService;

    @Autowired
    private IStuStudentService stuStudentService;

    @Autowired
    private DefaultCache defaultCache;

    @Autowired
    private GlobalExecutor globalExecutor;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FileServerProperties fileServerProperties;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public BonusApply getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Integer yearId = (Integer) params.remove("yearId");
        Integer gradeId = (Integer) params.remove("gradeId");
        Integer professionId = (Integer) params.remove("professionId");
        Integer approval = (Integer) params.remove("approval");

        // 查询可用的学生id
        List<StuStudent> stuStudents = stuStudentService.list(new QueryWrapper<StuStudent>()
                .eq("grade_id", gradeId)
                .eq("profession_id", professionId)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        List<Integer> studentIds = stuStudents.stream().map(StuStudent::getId).collect(Collectors.toList());
        if (studentIds.size() == 0) {
            return new PageUtils(new ArrayList<BonusApply>(), 0, 0, Integer.parseInt((String) params.get("page")));
        }

        IPage<BonusApply> page = this.page(
                new Query<BonusApply>().getPage(params),
                new QueryWrapper<BonusApply>()
                        .eq("approval", approval)
                        .eq("year_id", yearId)
                        .in("stu_student_id", studentIds)
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
                        .orderByAsc("stu_student_id")
        );

        List<BonusApply> originBonusApplies = page.getRecords();
        // 构建出bonusApplyVo的分页对象
        IPage<BonusApplyVo> bonusApplyVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        // 循环查询所有加分项信息
        List<BonusApplyVo> bonusApplyVos = originBonusApplies.stream().map(bonusApply -> {
            return getBonusApplyVo(bonusApply.getId());
        }).collect(Collectors.toList());

        bonusApplyVoPage.setRecords(bonusApplyVos);

        // 返回bonusApplyVo的分页对象
        return new PageUtils(bonusApplyVoPage);
    }

    /**
     * 持久化加分申请表信息到db
     *
     * @param user                用户信息
     * @param receiveBonusApplyVo 加分申请表
     */
    @Transactional
    @Override
    public void submitApplyInfo(StudentLoginVo user, ReceiveBonusApplyVo receiveBonusApplyVo) {
        // 检查加分项的名称和类别是否匹配
        BonusBonus bonusBonus = bonusBonusService.getOne(new QueryWrapper<BonusBonus>()
                .eq("category_id", receiveBonusApplyVo.getCategoryId())
                .eq("name", receiveBonusApplyVo.getBonusName())
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        if (bonusBonus == null) {
            throw new RuntimeException("加分项类别和加分项名称匹配不上");
        }

        // 检查学生提交的次数是否超出加分次数限制、检查学生分数是否超过上限
        checkInsideInfo(receiveBonusApplyVo, bonusBonus);

        // 检查用户是否重复提交图片
        checkRepeatSubmitFiles(user, receiveBonusApplyVo);

        List<MultipartFile> applyImages = receiveBonusApplyVo.getApplyImages();
        List<Integer> oosImageIds = applyImages.stream().map(multipartFile -> {
            OosImages oosImages = new OosImages();

            ResponseResult result;
            try {
                // 上传文件到图片服务器
                result = uploadFileToServer(multipartFile,
                        user,
                        "http://" + fileServerProperties.getIp() + ":" + fileServerProperties.getPort() + "/oos/oos-images/upload");
            } catch (Exception e) {
                log.error("文件提交失败");
                throw new RuntimeException("文件上传失败");
            }
            if (result == null) {
                log.error("上传文件失败，user=" + user.toString());
                throw new RuntimeException("上传文件失败");
            }
            // 利用Gson将result转为json
            OosUpload upload = toJson(result);

            oosImages.setMd5(upload.getMd5());
            oosImages.setUrl(upload.getDbUrl());
            oosImages.setPath(null);
            oosImages.setCreatedAt(LocalDateTime.now());
            oosImages.setCreatedBy(user.getStuName());
            oosImages.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());

            // 1.持久化oos_image
            oosImagesMapper.insert(oosImages);
            return oosImages.getId();
        }).collect(Collectors.toList());

        BonusApply bonusApply = new BonusApply();
        bonusApply.setYearId(receiveBonusApplyVo.getYearId());
        bonusApply.setCategoryId(receiveBonusApplyVo.getCategoryId());
        bonusApply.setBonusId(bonusBonus.getId());
        bonusApply.setStuStudentId(receiveBonusApplyVo.getStuStudentId());
        bonusApply.setScore(receiveBonusApplyVo.getBonusScore());
        bonusApply.setRemark(receiveBonusApplyVo.getRemark());
        bonusApply.setApproval(BonusConstant.BonusApplyEnum.NOT_APPROVED.getCode());
        bonusApply.setCreatedAt(new Date());
        bonusApply.setCreatedBy(user.getStuName());
        bonusApply.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
        // 2.持久化bonus_apply
        this.baseMapper.insert(bonusApply);
        // 拿到自增主键
        int bonusApplyId = bonusApply.getId();

        List<BonusApplyImage> bonusApplyImages = oosImageIds.stream().map(oosImageId -> {
            BonusApplyImage bonusApplyImage = new BonusApplyImage();
            bonusApplyImage.setBonusApplyId(bonusApplyId);
            bonusApplyImage.setOosImagesId(oosImageId);
            bonusApplyImage.setCreatedAt(LocalDateTime.now());
            bonusApplyImage.setCreatedBy(user.getStuName());
            bonusApplyImage.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());

            return bonusApplyImage;
        }).collect(Collectors.toList());

        // 3.持久化bonus_apply_image
        bonusApplyImageService.saveBatch(bonusApplyImages);

        StuStudent stuStudent = stuStudentService.getById(user.getId());
        // 先存后删
        // 删除学生端（根据学年）
        redisUtils.hashOperationDelHashKey(RedisKeyConstant.STUDENT_BONUS_APPLY_LIST_SINGLE_KEY + ":" + user.getId(), receiveBonusApplyVo.getYearId().toString());
        // 删除老师端的数据（删除未审核的）
        redisUtils.delete(RedisKeyConstant.TEACHER_BONUS_APPLY_LIST_ALL_KEY +
                ":" + receiveBonusApplyVo.getYearId() +
                "_" + stuStudent.getGradeId() +
                "_" + stuStudent.getProfessionId() +
                "_" + BonusConstant.BonusApplyEnum.NOT_APPROVED.getCode());

        log.info("提交申请表成功 bonusApplyId:{}, user:{}", bonusApplyId, user);
    }

    /**
     * 检查学生提交的次数是否超出的加分次数的限制、检查学生分数是否超过上限
     *
     * @param bonusApplyVo 加分表
     */
    private void checkInsideInfo(ReceiveBonusApplyVo bonusApplyVo, BonusBonus bonusBonus) {
        // 加分申请表 只要不是被拒绝的，都算一次该类的加分次数
        long countBonusApplies = this.count(new QueryWrapper<BonusApply>()
                .eq("year_id", bonusApplyVo.getYearId())
                .eq("stu_student_id", bonusApplyVo.getStuStudentId())
                .eq("bonus_id", bonusBonus.getId())
                .ne("approval", BonusConstant.BonusApplyEnum.REJECTED.getCode())
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        StuStudent stuStudent = stuStudentService.getById(bonusApplyVo.getStuStudentId());

        // 1.检查加分次数是否超过上限 (-1代表无限制提交）
        if (bonusBonus.getMaxTimes() != null && countBonusApplies >= bonusBonus.getMaxTimes()) {
            log.warn("student:" + stuStudent + " -> 加分项提交次数:" + countBonusApplies + " 超过上限:" + bonusBonus.getMaxTimes());
            throw new RuntimeException("该加分项提交次数已超过上限");
        }

        // 2.检查加分分数是否超过类别的上限
        List<BonusApply> bonusApplies = this.list(new QueryWrapper<BonusApply>()
                .eq("year_id", bonusApplyVo.getYearId())
                .eq("stu_student_id", bonusApplyVo.getStuStudentId())
                .eq("category_id", bonusApplyVo.getCategoryId())
                .ne("approval", BonusConstant.BonusApplyEnum.REJECTED.getCode())
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        // 存放类别id（类别id = 2）和该类别id对应的分数  -  只有一个值
        Map<Integer, BigDecimal> categoryIdAndScoreMap = new HashMap<>();
        bonusApplies.forEach(bonusApply -> {
            BigDecimal score = categoryIdAndScoreMap.get(bonusApply.getCategoryId()) == null
                    ? new BigDecimal("0.00")
                    : categoryIdAndScoreMap.get(bonusApply.getCategoryId());
            if (Objects.equals(bonusApply.getCategoryId(), BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode())) {
                score = score.add(new BigDecimal(String.valueOf(bonusApply.getScore())));
                categoryIdAndScoreMap.put(bonusApply.getCategoryId(), score.setScale(2, RoundingMode.HALF_UP));
            }
        });

        // 用户已经提交过的分数 + 正在提交的分数 > 上限   =>   报错
        BigDecimal categoryScore = categoryIdAndScoreMap.get(bonusBonus.getCategoryId());
        if (categoryScore == null) {
            return;
        }
        CategoryVo categoryVo = defaultCache.getCategoryVo(bonusBonus.getCategoryId());
        categoryScore = categoryScore.add(new BigDecimal(String.valueOf(bonusBonus.getScore()))
                .multiply(categoryVo.getWeight())    // 乘以权重
                .multiply(BonusConstant.WEIGHT_RATIO));   // 乘以百分比
        categoryIdAndScoreMap.put(bonusBonus.getCategoryId(), categoryScore);

        String gradeName = defaultCache.getIdAndGradeNameMap().get(stuStudent.getGradeId());
        // 判断类别分数是否超过上限
        categoryIdAndScoreMap.forEach((categoryId, score) -> {
            if (Objects.equals(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode(), categoryId)
                    && BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getGradeAndUpperLimitMap().get(gradeName)
                    .compareTo(score.add(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getBenchmarkScore())) < 0) {
                log.warn("student:" + stuStudent +
                        " -> 学生分数:" + score +
                        "_" + BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getName() +
                        "上限分数:" + BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getGradeAndUpperLimitMap().get(gradeName) +
                        " 已超过上限");
                throw new RuntimeException(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getName() + "分数超过上限");
            }
        });
    }

    /**
     * 查询用户指定学年的加分申请表信息（用户模块）
     *
     * @param yearId 学年id
     * @param userId 用户信息id
     * @return 学生申请表信息
     */
    @Override
    public StuBonusApplyVo getStuBonusApplyVoByYearId(Integer yearId, Integer userId) {
        // 1.根据学年、学生id查询学生本年所有的bonus_apply
        List<BonusApply> bonusApplies = list(new QueryWrapper<BonusApply>()
                .eq("year_id", yearId)
                .eq("stu_student_id", userId)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        // 2.根据bonus_apply查询用户的StuBonusVo
        List<StuBonusVo> stuBonusVos = bonusComprehensiveScoreService.getStuBonusVos(bonusApplies);

        YearVo yearVo = defaultCache.getYearVo(yearId);

        // 将申请表id从大到小排序
        stuBonusVos.sort(((o1, o2) -> o2.getBonusApplyId() - o1.getBonusApplyId()));

        // 3.填充数据到StuBonusApplyVo
        StuBonusApplyVo stuBonusApplyVo = new StuBonusApplyVo();
        stuBonusApplyVo.setYearId(yearVo.getYearId());
        stuBonusApplyVo.setYearName(yearVo.getYearName());
        stuBonusApplyVo.setStuBonusVos(stuBonusVos);

        StuStudent student = stuStudentService.getById(userId);
        log.info("student:" + student + " -> 查询信息StuBonusApplyVo");
        return stuBonusApplyVo;
    }

    /**
     * 删除申请表（用户模块）
     *
     * @param bonusApplyId 申请表id
     * @param updateName   修改者名字
     */
    @Transactional
    @Override
    public void deleteBonusApply(Integer bonusApplyId, String updateName) {
        // 1.根据bonus_apply_id查询bonus_apply表
        BonusApply bonusApply = bonusApplyService.getById(bonusApplyId);

        if (bonusApply != null) {
            // 更新加分申请表
            this.update(new UpdateWrapper<BonusApply>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", new Date())
                    .set("updated_by", updateName)
                    .eq("id", bonusApplyId));

            // 更新该加分项id有关联的bonus_apply_image的deleted为1（bonus_apply_id可以关联多个图片）
            bonusApplyImageService.update(new UpdateWrapper<BonusApplyImage>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .set("updated_at", LocalDateTime.now())
                    .set("updated_by", updateName)
                    .eq("bonus_apply_id", bonusApplyId));

            List<Integer> oosImagesByBonusApplyId = bonusApplyImageService.getOosImagesByBonusApplyId(bonusApplyId);

            // 删除图片，设置为deleted = 1
            oosImagesService.update(new UpdateWrapper<OosImages>()
                    .set("deleted", ProjectConstant.DeletedStatusEnum.DELETED.getCode())
                    .in("id", oosImagesByBonusApplyId));

            // 删除redis中的数据
            StuStudent student = stuStudentService.getById(bonusApply.getStuStudentId());
            // 删除学生端（根据学年）
            redisUtils.hashOperationDelHashKey(RedisKeyConstant.STUDENT_BONUS_APPLY_LIST_SINGLE_KEY + ":" + student.getId(), bonusApply.getYearId().toString());
            // 删除老师端的数据（删除已驳回的加分申请表）
            redisUtils.delete(RedisKeyConstant.TEACHER_BONUS_APPLY_LIST_ALL_KEY +
                    ":" + bonusApply.getYearId() +
                    "_" + student.getGradeId() +
                    "_" + student.getProfessionId() +
                    "_" + BonusConstant.BonusApplyEnum.REJECTED.getCode());

            log.info("student:" + student + " -> 删除加分项bonusApply:" + bonusApply);
        } else {
            throw new RuntimeException("找不到指定申请表");
        }
    }

    /**
     * 查询加分申请表
     *
     * @param bonusApplyId 加分申请表id
     * @return 前端加分申请表Vo对象
     */
    @Override
    public BonusApplyVo getBonusApplyVo(Integer bonusApplyId) {
        BonusApply bonusApply = bonusApplyService.getById(bonusApplyId);

        BonusApplyVo bonusApplyVo = new BonusApplyVo();
        bonusApplyVo.setId(bonusApply.getId());
        bonusApplyVo.setCreateAt(bonusApply.getCreatedAt());

        // 拿到缓存的学年信息
        YearVo yearVo = defaultCache.getYearVo(bonusApply.getYearId());
        bonusApplyVo.setYearVo(yearVo);

        // 拿到缓存的类别信息
        CategoryVo categoryVo = defaultCache.getCategoryVo(bonusApply.getCategoryId());
        bonusApplyVo.setCategoryVo(categoryVo);

        // 查询加分项信息
        BonusBonus bonusBonus = bonusBonusService.getById(bonusApply.getBonusId());
        // 设置加分项名称
        bonusApplyVo.setBonusName(bonusBonus == null ? null : bonusBonus.getName());

        // 异步查询学生信息（期间包括查询学院、年级、专业、班级等信息
        StuStudentVo stuStudentVo = stuStudentService.getStudentVo(bonusApply.getStuStudentId());
        bonusApplyVo.setStudentVo(stuStudentVo);

        // 查询oosImageIds
        List<BonusApplyImage> bonusApplyImages = bonusApplyImageService.list(new QueryWrapper<BonusApplyImage>()
                .eq("bonus_apply_id", bonusApplyId)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        List<Integer> oosImagesIds = bonusApplyImages.stream().map(bonusApplyImage -> {
            return bonusApplyImage.getOosImagesId();
        }).collect(Collectors.toList());
        bonusApplyVo.setOosImagesIds(oosImagesIds);

        // 只有已审核的申请表，bonusScore才会有分
        if (bonusApply.getApproval() == BonusConstant.BonusApplyEnum.APPROVED.getCode()) {
            bonusApplyVo.setBonusScore(bonusApply.getScore()
                    .multiply(categoryVo.getWeight())
                    .multiply(BonusConstant.WEIGHT_RATIO)
                    .setScale(2, RoundingMode.HALF_UP));
        } else {
            bonusApplyVo.setBonusScore(new BigDecimal("0.00"));
        }
        // 设置申请分数
        bonusApplyVo.setApplyScore(bonusApply.getScore());

        // 填充加分项的信息  设置该加分申请表的成绩（类别 * 权重）
        bonusApplyVo.setId(bonusApply.getId());
        bonusApplyVo.setRemark(bonusApply.getRemark());
        bonusApplyVo.setApproval(bonusApply.getApproval());
        bonusApplyVo.setApprovalBy(bonusApply.getApprovalBy());
        bonusApplyVo.setApprovalAt(bonusApply.getApprovalAt());
        bonusApplyVo.setApprovalComments(bonusApply.getApprovalComments());

        return bonusApplyVo;
    }

    /**
     * 获取加分申请表（条件：学年、年级、专业、审核状态）
     *
     * @param yearId       学年id
     * @param gradeId      年级id
     * @param professionId 专业id
     * @param approval     审核状态
     * @return 需要申请的加分申请表
     */
    @Override
    public List<BonusApplyVo> getApprovalInfo(Integer yearId, Integer gradeId, Integer professionId, Integer approval) {
        // 1.通过学生id查询学生表
        List<BonusApply> bonusApplies = this.list(new QueryWrapper<BonusApply>()
                .eq("year_id", yearId)
                .eq("approval", approval)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        // map : [bonus_apply_id - student_id]
        Map<Integer, Integer> baAndStudentIdsMap = bonusApplies.stream()
                .collect(Collectors.toMap(BonusApply::getId, BonusApply::getStuStudentId));

        // 2.gradeId、professionId符合条件
        List<Integer> realBonusApplyIds = new CopyOnWriteArrayList<>();
        List<Future<Void>> studentFutures = new CopyOnWriteArrayList<>();
        baAndStudentIdsMap.forEach((bonusApplyId, studentId) -> {
            CompletableFuture<Void> studentAsync = CompletableFuture.runAsync(() -> {
                StuStudent stuStudent = stuStudentService.getOne(new QueryWrapper<StuStudent>()
                        .eq("id", studentId)
                        .eq("grade_id", gradeId)
                        .eq("profession_id", professionId)
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
                // 符合grade_id、profession_id的bonusApplyId
                if (stuStudent != null) {
                    // 异步存在并发写入问题   CopyOnWriteArrayList的add方法是加锁了synchronized
                    realBonusApplyIds.add(bonusApplyId);
                }
            }, executor);
            studentFutures.add(studentAsync);
        });

        // 等待异步 -> 填充realBonusApplyIds
        for (Future<Void> future : studentFutures) {
            globalExecutor.getAsync(future, ProjectConstant.BLEND_ASYNC, "获取学生信息");
        }

        // 利用realBonusApplyIds拿到所有审批表
        return realBonusApplyIds.stream().map(bonusApplyId -> {
            return getBonusApplyVo(bonusApplyId);
        }).collect(Collectors.toList());
    }

    /**
     * 管理员审核加分申请表
     *
     * @param name     审核者姓名
     * @param verifyVo 审核对象Vo
     */
    @Transactional
    @Override
    public void verify(String name, VerifyVo verifyVo) {
        if (verifyVo.getApproval() == BonusConstant.BonusApplyEnum.APPROVED.getCode()
                || verifyVo.getApproval() == BonusConstant.BonusApplyEnum.NOT_APPROVED.getCode()
                || verifyVo.getApproval() == BonusConstant.BonusApplyEnum.REJECTED.getCode()
                || verifyVo.getApproval() == BonusConstant.BonusApplyEnum.PRE_PASS.getCode()) {
            // 先判断该申请表是否已经申请过了
            BonusApply bonusApply = bonusApplyService.getById(verifyVo.getBonusApplyId());
            if (bonusApply.getApproval() == BonusConstant.BonusApplyEnum.NOT_APPROVED.getCode()
                    || bonusApply.getApproval() == BonusConstant.BonusApplyEnum.PRE_PASS.getCode()) {
//                CategoryVo categoryVo = defaultCache.getCategoryVo(bonusApply.getCategoryId());
//                // 老师给的分数不能超过上限
//                BigDecimal actualBonusScore = verifyVo.getBonusScore().multiply(BonusConstant.WEIGHT_RATIO).multiply(categoryVo.getWeight());
//
                StuStudent student = stuStudentService.getById(bonusApply.getStuStudentId());
//                String gradeName = defaultCache.getIdAndGradeNameMap().get(student.getGradeId());
//
//                // 超过上限
//                if (actualBonusScore.compareTo(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getGradeAndUpperLimitMap().get(gradeName)) > 0) {
//                    throw new RuntimeException("申请分数不能超过上限");
//                }
                try {
                    UpdateWrapper<BonusApply> updateWrapper = new UpdateWrapper<BonusApply>()
                            .set("approval", verifyVo.getApproval())
                            .set("approval_comments", verifyVo.getApprovalComments())
                            .set("approval_by", name)
                            .set("approval_at", new Date())
                            .set("updated_at", new Date())
                            .set("updated_by", name)
                            .set("score", verifyVo.getBonusScore())
                            .eq("id", verifyVo.getBonusApplyId());

                    // 修改加分申请表的状态，该方法执行必须成功
                    this.update(updateWrapper);

                    // 删除redis中getById的数据
                    redisUtils.delete("aop:getById:BonusApplyServiceImpl:" + verifyVo.getBonusApplyId());
                    // 删除redis中学生加分申请表的信息（删除学年）
                    redisUtils.hashOperationDelHashKey(RedisKeyConstant.STUDENT_BONUS_APPLY_LIST_SINGLE_KEY + ":" + student.getId(), bonusApply.getYearId().toString());
                    // 删除老师端year_grade_profession_approval的数据（删除审核后的）
                    redisUtils.delete(RedisKeyConstant.TEACHER_BONUS_APPLY_LIST_ALL_KEY +
                            ":" + bonusApply.getYearId() +
                            "_" + student.getGradeId() +
                            "_" + student.getProfessionId() +
                            "_" + verifyVo.getApproval());
                    // 删除老师端year_grade_profession_approval的数据（删除审核前的）
                    redisUtils.delete(RedisKeyConstant.TEACHER_BONUS_APPLY_LIST_ALL_KEY +
                            ":" + bonusApply.getYearId() +
                            "_" + student.getGradeId() +
                            "_" + student.getProfessionId() +
                            "_" + bonusApply.getApproval());
                } catch (Exception e) {
                    throw new RuntimeException("申请表审核失败!请重试");
                }
            } else {
                throw new RuntimeException("该申请表已审核过了");
            }
        } else {
            throw new RuntimeException("提交的审核状态异常");
        }
    }

    /**
     * 老师端 - 修改加分申请表
     *
     * @param modifyBonusApplyVo 前端加分申请表Vo对象
     * @param teacherName        老师名称
     */
    @Transactional
    @Override
    public void modifyBonusApplyWithTeacher(ModifyBonusApplyVo modifyBonusApplyVo, String teacherName) {
        // 1.直接update两个表（bonus_apply、bonus_bonus）
        UpdateWrapper<BonusBonus> bonusBonusUpdateWrapper = new UpdateWrapper<BonusBonus>()
                .set("score", new BigDecimal(modifyBonusApplyVo.getScore()))
                .set("updated_at", LocalDateTime.now())
                .set("updated_by", teacherName)
                .eq("id", modifyBonusApplyVo.getBonusId());
        if (modifyBonusApplyVo.getBonusName() != null) {
            bonusBonusUpdateWrapper.set("name", modifyBonusApplyVo.getBonusName());
        }
        // 更新加分项表
        bonusBonusService.update(bonusBonusUpdateWrapper);

        // 查询类别表，得到类别所占权重，计算出该申请表的分数，更新到bonus_apply中
        BonusCategory bonusCategory = categoryService.getById(modifyBonusApplyVo.getCategoryId());

        // 计算出该加分申请表应该加的分数是多少
        BigDecimal totalScore = new BigDecimal(modifyBonusApplyVo.getScore())
                .multiply(bonusCategory.getWeights())
                .multiply(BonusConstant.WEIGHT_RATIO);

        UpdateWrapper<BonusApply> bonusApplyUpdateWrapper = new UpdateWrapper<>();
        if (modifyBonusApplyVo.getRemark() != null) {
            bonusApplyUpdateWrapper.set("remark", modifyBonusApplyVo.getRemark());
        }
        // 更新加分申请表
        bonusApplyService.update(bonusApplyUpdateWrapper
                .set("score", totalScore.setScale(2, RoundingMode.HALF_UP))
                .eq("id", modifyBonusApplyVo.getBonusApplyId()));
    }

    /**
     * 查询用户指定的加分申请表id的信息
     *
     * @param bonusApplyId 加分申请表id
     * @return 加分申请表信息
     */
    @Override
    public BonusApplyVo getBonusApplyVoByBonusApplyId(Integer bonusApplyId) {
        return getBonusApplyVo(bonusApplyId);
    }


    /**
     * 查询用户所有学年的加分申请表信息
     *
     * @param userId 用户id
     * @return 所有加分申请表信息
     */
    @Override
    public List<StuBonusApplyVo> getAllStuBonusApplyVo(int userId) {
        List<StuBonusApplyVo> stuBonusApplyVos = new CopyOnWriteArrayList<>();
        // 1.查到所有学年的信息
        List<YearVo> yearVoCache = defaultCache.getYearVoCache();

        // 2.异步编排获取每个学年的StuBonusApplyVo
        List<Future<Void>> futures = new ArrayList<>();
        for (YearVo yearVo : yearVoCache) {
            CompletableFuture<Void> supplyAsync = CompletableFuture.runAsync(() -> {
                List<StuBonusVo> stuBonusVos = bonusApplyMapper.getStuBonusVosByStuAndYearId(userId, yearVo.getYearId());

                stuBonusVos.forEach(stuBonusVo -> {
                    CategoryVo categoryVo = defaultCache.getCategoryVo(stuBonusVo.getCategoryId());
                    stuBonusVo.setCategoryVo(categoryVo);
                    List<Integer> oosImagesIds = bonusApplyImageService.getOosImagesByBonusApplyId(stuBonusVo.getBonusApplyId());

                    stuBonusVo.setOosImagesIds(oosImagesIds);
                    // 只有已审核的bonusScore才会有分数
                    if (stuBonusVo.getApproval() == BonusConstant.BonusApplyEnum.APPROVED.getCode()) {
                        stuBonusVo.setBonusScore(stuBonusVo.getApplyScore()
                                .multiply(categoryVo.getWeight())
                                .multiply(BonusConstant.WEIGHT_RATIO)
                                .setScale(2, RoundingMode.HALF_UP));
                    } else {
                        stuBonusVo.setBonusScore(new BigDecimal("0.00"));
                    }
                });

                StuBonusApplyVo stuBonusApplyVo = new StuBonusApplyVo();
                stuBonusApplyVo.setYearId(yearVo.getYearId());
                stuBonusApplyVo.setYearName(yearVo.getYearName());
                stuBonusApplyVo.setStuBonusVos(stuBonusVos);

                // 异步存在并发写入问题（这样可以使得线程池里面没有大数据）
                stuBonusApplyVos.add(stuBonusApplyVo);
            }, executor);

            futures.add(supplyAsync);
        }

        for (Future<Void> future : futures) {
            // 获取异步任务结果
            globalExecutor.getAsync(future, ProjectConstant.BLEND_ASYNC, "获取加分申请表");

        }

        return stuBonusApplyVos;
    }

    /**
     * 教师通过学生学号查询该学生的加分申请表
     *
     * @param yearId    学年id
     * @param stuNumber 学生学号
     * @return 加分申请表信息
     */
    @Override
    public List<BonusApplyVo> getBonusApplyByStuNumberYearId(Integer yearId, String stuNumber, Integer approval) {
        StuStudent stuStudent = stuStudentService.getOne(new QueryWrapper<StuStudent>()
                .eq("stu_number", stuNumber)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        List<BonusApply> bonusApplies = this.list(new QueryWrapper<BonusApply>()
                .eq("stu_student_id", stuStudent.getId())
                .eq("year_id", yearId)
                .eq("approval", approval)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        List<BonusApplyVo> bonusApplyVos = new CopyOnWriteArrayList<>();

        // 填充bonusApplyVos
        for (BonusApply bonusApply : bonusApplies) {
            BonusApplyVo bonusApplyVo = getBonusApplyVo(bonusApply.getId());
            bonusApplyVos.add(bonusApplyVo);
        }

        return bonusApplyVos;
    }

    /**
     * 上传文件到图片服务器
     *
     * @param file           文件
     * @param studentLoginVo 用户信息
     * @param url            图片服务器地址
     * @return 结果
     * @throws Exception
     */
    private ResponseResult uploadFileToServer(MultipartFile file, StudentLoginVo studentLoginVo, String url) throws Exception {
        if (file.isEmpty()) {
            throw new RuntimeException("文件材料未提交");
        }
        ResponseResult result = null;
        File file1 = null;
        try {
            //转换为file
            file1 = IOUtils.multipartFileToFile(file);
            FileSystemResource resource = new FileSystemResource(file1);
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            //参数
            param.add("file", resource);
            param.add("user", studentLoginVo);

            HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(param);
            ResponseEntity<ResponseResult> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, ResponseResult.class);
            result = responseEntity.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file1 != null) {
                //最后要删除
                file1.delete();
            }
        }
        return result;
    }

    /**
     * 检查用户是否重复提交图片
     *
     * @param user         用户信息
     * @param bonusApplyVo 申请表信息
     */
    @CatchException(value = "处理图片异常")
    private void checkRepeatSubmitFiles(StudentLoginVo user, ReceiveBonusApplyVo bonusApplyVo) {
        // 1.对所有图片进行md5加密
        List<MultipartFile> applyImages = bonusApplyVo.getApplyImages();
        if (applyImages == null || applyImages.size() == 0) {
            log.error("studentLoginVo:" + user + " -> 图片材料未提交");
            throw new RuntimeException("图片材料必须提交");
        }

        List<String> oosImagesMd5 = applyImages.stream().map(applyImage -> {
            long size = applyImage.getSize();
            if (size > ProjectConstant.FILE_UPLOAD_SIZE) {
                log.error("studentLoginVo:" + user + " -> 提交的文件大小:" + size + "b=" + (size / 1024) + "kb=" + (size / 1024 / 1024) + "m");
                throw new RuntimeException("提交的图片材料不能大于" + (ProjectConstant.FILE_UPLOAD_SIZE / 1024 / 1024) + "M");
            }

            try {
                // 图片md5加密
                return DigestUtils.md5DigestAsHex(applyImage.getBytes());
            } catch (IOException e) {
                log.error("studentLoginVo:" + user + " -> 图片加密异常");
                throw new RuntimeException("处理图片异常");
            }
        }).collect(Collectors.toList());

        // 2.根据md5 + created_by，判断是否存在
        List<OosImages> oosImagesList = oosImagesService.list(new QueryWrapper<OosImages>()
                .eq("created_by", user.getStuName())
                .in("md5", oosImagesMd5)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        if (oosImagesList != null && oosImagesList.size() > 0) {
            log.error("studentLoginVo:" + user + " -> 重复提交文件");
            throw new RuntimeException("重复提交文件");
        }
    }

    /**
     * 将ResponseResult对象转为OosUpload
     *
     * @param result ResponseResult对象
     * @return OosUpload
     */
    private OosUpload toJson(ResponseResult result) {
        Gson gson = new Gson();
        String json = gson.toJson(result.getData().values());
        List<OosUpload> oosUploads = gson.fromJson(json, new TypeToken<List<OosUpload>>() {
        }.getType());
        return oosUploads.get(0);
    }
}
