package cn.ccsu.cecs.bonus.service.impl;

import cn.ccsu.cecs.admin.service.AdminUserService;
import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.entity.BonusBonus;
import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.entity.BonusComprehensiveScore;
import cn.ccsu.cecs.bonus.mapper.BonusComprehensiveScoreMapper;
import cn.ccsu.cecs.bonus.service.*;
import cn.ccsu.cecs.bonus.vo.*;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.entity.StudentCoreInfo;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import cn.ccsu.cecs.common.utils.*;
import cn.ccsu.cecs.config.ThreadConfig;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.mapper.StuStudentMapper;
import cn.ccsu.cecs.student.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * <p>
 * 综合成绩表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-10
 */
@Slf4j
@Service
public class BonusComprehensiveScoreServiceImpl extends ServiceImpl<BonusComprehensiveScoreMapper, BonusComprehensiveScore> implements IBonusComprehensiveScoreService {

    @Autowired
    IBonusBonusService bonusBonusService;

    @Autowired
    IBonusApplyService bonusApplyService;

    @Autowired
    IStuStudentService stuStudentService;

    @Autowired
    IBonusCategoryService bonusCategoryService;

    @Autowired
    IBonusYearService bonusYearService;

    @Autowired
    IStuProfessionService professionService;

    @Autowired
    IStuClassService classService;

    @Autowired
    IStuCollegeService collegeService;

    @Autowired
    IStuGradeService gradeService;

    @Autowired
    IBonusApplyImageService bonusApplyImageService;

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    StuStudentMapper stuStudentMapper;

    @Autowired
    DefaultCache defaultCache;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BonusComprehensiveScore> page = this.page(
                new Query<BonusComprehensiveScore>().getPage(params),
                new QueryWrapper<BonusComprehensiveScore>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );

        return new PageUtils(page);
    }

    /**
     * 查询学生所在班级的所有学生成绩（通过学年id）  -- 支持分页
     *
     * @param userId 学生id
     * @param yearId 学年id
     * @param params 分页参数
     * @return 学生班级信息
     */
    @Override
    public List<StuScoreVo> getStuClassTotalScoreByYearPage(Integer userId, Integer yearId, Map<String, Object> params) {
        return getStuClassTotalScoreByYear(userId, yearId, params);
    }

    /**
     * 异步编排获取加分项信息
     *
     * @param bonusApplies 加分表list
     * @return 学生加分项列表
     */
    @Override
    public List<StuBonusVo> getStuBonusVos(List<BonusApply> bonusApplies) {
        List<StuBonusVo> stuBonusVos = bonusApplies.stream().map(bonusApply -> {
            StuBonusVo stuBonusVo = new StuBonusVo();
            stuBonusVo.setBonusApplyId(bonusApply.getId());
            stuBonusVo.setApprovalComments(bonusApply.getApprovalComments());
            stuBonusVo.setApprovalTime(bonusApply.getApprovalAt());
            stuBonusVo.setApproval(bonusApply.getApproval());
            stuBonusVo.setRemark(bonusApply.getRemark());
            stuBonusVo.setSubmitTime(bonusApply.getCreatedAt());

            // 根据学生id查询学生信息
            StuStudent student = stuStudentService.getById(bonusApply.getStuStudentId());

            if (student != null) {
                stuBonusVo.setStuNumber(student.getStuNumber());
                stuBonusVo.setStuName(student.getStuName());
            }

            // 根据类别id查询类别信息
            CategoryVo cacheCategoryVo = defaultCache.getCategoryVo(bonusApply.getCategoryId());
            stuBonusVo.setCategoryVo(cacheCategoryVo);

            // 根据加分项id查询加分项
            BonusBonus bonusBonus = bonusBonusService.getById(bonusApply.getBonusId());
            // 如果加分项没有，说明应该是文件导入进来的，这样就给他加分项名称设置为加分申请表的备注
            stuBonusVo.setBonusName(bonusBonus == null ? bonusApply.getRemark() : bonusBonus.getName());

            // 设置申请分数
            stuBonusVo.setApplyScore(bonusApply.getScore());

            // 根据加分申请表id查询bonus_apply_image
            List<Integer> oosImagesIds = bonusApplyImageService.getOosImagesByBonusApplyId(bonusApply.getId());

            // 设置oosImagesId
            stuBonusVo.setOosImagesIds(oosImagesIds);

            // 只有已审核的申请表，bonusScore才会有分
            if (bonusApply.getApproval() == BonusConstant.BonusApplyEnum.APPROVED.getCode()) {
                stuBonusVo.setBonusScore(bonusApply.getScore()
                        .multiply(cacheCategoryVo.getWeight())
                        .multiply(BonusConstant.WEIGHT_RATIO)
                        .setScale(2, RoundingMode.HALF_UP));
            } else {
                stuBonusVo.setBonusScore(new BigDecimal("0.00"));
            }

            // 设置审核时间
            stuBonusVo.setApprovalTime(bonusApply.getApprovalAt());
            stuBonusVo.setApprovalComments(bonusApply.getApprovalComments());
            return stuBonusVo;
        }).collect(Collectors.toList());

        return stuBonusVos;
    }

    /**
     * 异步编排获取学年、学院、年级、专业、班级信息
     *
     * @param stuStudent 学生信息
     * @param yearId     学年id
     * @return 学生核心信息
     */
    private StudentCoreInfo asyncAndGetStudentCoreInfo(StuStudent stuStudent, Integer yearId) {
        YearVo yearVo = defaultCache.getYearVo(yearId);
        StudentCoreInfo studentCoreInfo = stuStudentMapper.getStudentCoreInfo(stuStudent.getId());
        studentCoreInfo.setYearId(yearVo.getYearId());
        studentCoreInfo.setYearName(yearVo.getYearName());

        return studentCoreInfo;
    }

    /**
     * 方法重载
     *
     * @param userId 用户id
     * @param yearId 学年id
     * @param params 分页参数
     * @return 结果
     */
    @Override
    public List<StuScoreVo> getStuProfessionTotalScoreByYearPage(Integer userId, Integer yearId, Map<String, Object> params) {
        // 先复制一份map
        Map<String, Object> map = Map.copyOf(params);

        // 1.查询学生表拿到跟本学生同属于一个professionId下的studentId
        StuStudent student = stuStudentService.getById(userId);
        IPage<BonusComprehensiveScore> pageUtils = null;
        if (student != null) {
            pageUtils = this.page(
                    new Query<BonusComprehensiveScore>().getPage(params),
                    new QueryWrapper<BonusComprehensiveScore>()
                            .eq("profession_id", student.getProfessionId())
                            .eq("grade_id", student.getGradeId())
                            .eq("year_id", yearId)
                            .orderByDesc("score")
                            .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        }

        if (pageUtils == null || pageUtils.getRecords().size() == 0) {
            return null;
        }

        // 拿到同专业所有学生的总成绩信息
        List<BonusComprehensiveScore> comprehensiveList = pageUtils.getRecords();

        // 查询所有类别信息
        List<BonusCategory> bonusCategories = defaultCache.getBonusCategoriesCache();

        // 存储学生信息
        List<StuScoreVo> stuScoreVos = new ArrayList<>();
        for (BonusComprehensiveScore bonusComprehensiveScore : comprehensiveList) {
            // 1.拿到学生信息
            StuStudent stuStudent = stuStudentService.getById(bonusComprehensiveScore.getStuStudentId());
            // 2.拿到学生核心信息
            StudentCoreInfo studentCoreInfo = asyncAndGetStudentCoreInfo(stuStudent, yearId);
            // 3.拿到加分项类别分数信息
            List<BonusCategoryScoreVo> studentBonusCategoryScoreVos = getBonusCategoryScoreVos(bonusCategories, stuStudent, yearId);
            // 4.设置学生成绩信息
            StuScoreVo stuScoreVo = setUpStuScoreVo(stuStudent, studentCoreInfo, studentBonusCategoryScoreVos);
            stuScoreVos.add(stuScoreVo);
        }

        dealStuScoreRank(stuScoreVos, map);
        return stuScoreVos;
    }

    /**
     * 获取学生所在班级的总成绩
     *
     * @param userId 学生id
     * @param yearId 学年id
     * @param params 分页参数
     * @return 结果
     */
    @Override
    public List<StuScoreVo> getStuClassTotalScoreByYear(Integer userId, Integer yearId, Map<String, Object> params) {
        // 复制一下map
        Map<String, Object> map = Map.copyOf(params);

        StuStudent student = stuStudentService.getById(userId);
        // 数据分页
        IPage<BonusComprehensiveScore> page = this.page(
                new Query<BonusComprehensiveScore>().getPage(params),
                new QueryWrapper<BonusComprehensiveScore>()
                        .eq("year_id", yearId)
                        .eq("grade_id", student.getGradeId())
                        .eq("profession_id", student.getProfessionId())
                        .eq("college_id", student.getCollegeId())
                        .eq("class_id", student.getClassId())
                        .orderByDesc("score"));   // 按成绩降序

        // 1.查询同班级的所有学生id   同班级、同专业、同年级
        List<BonusComprehensiveScore> bonusComprehensiveScores = page.getRecords();

        if (bonusComprehensiveScores.size() == 0) {
            return null;
        }

        // 拿到同班级的所有学生id
        List<Integer> studentIds = bonusComprehensiveScores.stream().map(BonusComprehensiveScore::getStuStudentId).collect(Collectors.toList());
        // 查询所有类别信息
        List<BonusCategory> bonusCategories = defaultCache.getBonusCategoriesCache();

        // 2.通过学生id，查询班级的每个学生的成绩
        List<StuScoreVo> stuScoreVos = new ArrayList<>();
        for (Integer studentId : studentIds) {
            // 0.拿到学生信息
            StuStudent stuStudent = stuStudentService.getById(studentId);
            // 1.异步获取学生核心信息
            StudentCoreInfo studentCoreInfo = asyncAndGetStudentCoreInfo(stuStudent, yearId);
            // 2.获取加分项信息
            List<BonusCategoryScoreVo> bonusCategoryScoreVos = getBonusCategoryScoreVos(bonusCategories, stuStudent, yearId);
            // 3.设置StuScoreVo信息
            StuScoreVo stuScoreVo = setUpStuScoreVo(stuStudent, studentCoreInfo, bonusCategoryScoreVos);

            stuScoreVos.add(stuScoreVo);
        }

        // 处理学生成绩排名
        dealStuScoreRank(stuScoreVos, map);
        return stuScoreVos;
    }

    /**
     * 设置学生成绩信息
     *
     * @param stuStudent                   学生信息
     * @param studentCoreInfo              学生核心信息
     * @param studentBonusCategoryScoreVos 学生加分类别信息
     * @return 学生成绩对象Vo
     */
    private StuScoreVo setUpStuScoreVo(StuStudent stuStudent, StudentCoreInfo studentCoreInfo, List<BonusCategoryScoreVo> studentBonusCategoryScoreVos) {
        StuScoreVo stuScoreVo = new StuScoreVo();
        BigDecimal totalScore = new BigDecimal("0.00");
        if (studentBonusCategoryScoreVos != null) {
            for (BonusCategoryScoreVo bonusCategoryVo : studentBonusCategoryScoreVos) {
                totalScore = totalScore.add(bonusCategoryVo.getScore()).setScale(2, RoundingMode.HALF_UP);
            }
        }

        stuScoreVo.setYearName(studentCoreInfo.getYearName());
        stuScoreVo.setCollegeName(studentCoreInfo.getCollegeName());
        stuScoreVo.setProfessionName(studentCoreInfo.getProfessionName());
        stuScoreVo.setGradeName(studentCoreInfo.getGradeName());
        stuScoreVo.setClassName(studentCoreInfo.getClassName());
        stuScoreVo.setStuNumber(stuStudent.getStuNumber());
        stuScoreVo.setStuName(stuStudent.getStuName());
        stuScoreVo.setStuScore(totalScore);
        stuScoreVo.setBonusCategoryScoreVos(studentBonusCategoryScoreVos);

        return stuScoreVo;
    }

    /**
     * 查询指定学年的学生所在年级、专业的成绩信息
     *
     * @param yearId       学年id
     * @param gradeId      年级id
     * @param professionId 专业id
     * @return 结果
     */
    @Override
    public List<StuScoreVo> getStudentScore(Integer yearId, Integer gradeId, Integer professionId, Map<String, Object> params) {
        // 先复制一份map
        Map<String, Object> map = Map.copyOf(params);

        IPage<BonusComprehensiveScore> page = this.page(
                new Query<BonusComprehensiveScore>().getPage(params),
                new QueryWrapper<BonusComprehensiveScore>()
                        .eq("profession_id", professionId)
                        .eq("grade_id", gradeId)
                        .eq("year_id", yearId)
                        .orderByDesc("score")
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );
        List<BonusComprehensiveScore> bonusComprehensiveScores = page.getRecords();
        if (bonusComprehensiveScores.size() == 0) {
            return null;
        }

        // 拿到同班级的所有学生id
        List<Integer> studentIds = bonusComprehensiveScores.stream().map(BonusComprehensiveScore::getStuStudentId).collect(Collectors.toList());

        // 查询所有类别信息
        List<BonusCategory> bonusCategories = defaultCache.getBonusCategoriesCache();

        // 2.通过学生id，查询班级的每个学生的成绩
        List<StuScoreVo> stuScoreVos = new ArrayList<>();
        for (Integer studentId : studentIds) {
            try {
                // 0.拿到学生信息
                StuStudent stuStudent = stuStudentService.getById(studentId);
                // 1.异步获取学生核心信息
                StudentCoreInfo studentCoreInfo = asyncAndGetStudentCoreInfo(stuStudent, yearId);
                // 2.获取加分项信息
                List<BonusCategoryScoreVo> bonusCategoryScoreVos = getBonusCategoryScoreVos(bonusCategories, stuStudent, yearId);
                // 3.设置StuScoreVo信息
                StuScoreVo stuScoreVo = setUpStuScoreVo(stuStudent, studentCoreInfo, bonusCategoryScoreVos);

                stuScoreVos.add(stuScoreVo);
            } catch (Exception e) {
                log.error("学生id:" + studentId);
            }
        }

        // 处理学生成绩排名
        dealStuScoreRank(stuScoreVos, map);
        return stuScoreVos;
    }

    /**
     * 获取学生加分项类别的分类分数
     *
     * @param bonusCategories 加分项类别信息
     * @param stuStudent      学生信息
     * @param yearId          学年id
     * @return 学生加分项类别的分类分数
     */
    private List<BonusCategoryScoreVo> getBonusCategoryScoreVos(List<BonusCategory> bonusCategories, StuStudent stuStudent, Integer yearId) {
        BonusComprehensiveScore comprehensiveScore = this.getOne(new QueryWrapper<BonusComprehensiveScore>()
                .eq("year_id", yearId)
                .eq("stu_student_id", stuStudent.getId()));
        List<BonusCategoryScoreVo> bonusCategoryScoreVos = new ArrayList<>();
        for (BonusCategory bonusCategory : bonusCategories) {
            BonusCategoryScoreVo bonusCategoryScoreVo = new BonusCategoryScoreVo();
            CategoryVo categoryVo = new CategoryVo();
            categoryVo.setId(bonusCategory.getId());
            categoryVo.setName(bonusCategory.getName());
            categoryVo.setWeight(bonusCategory.getWeights());

            bonusCategoryScoreVo.setCategoryVo(categoryVo);
            // 分类别设置类别分数
            if (Objects.equals(bonusCategory.getName(), BonusConstant.CategoryWeightEnum.BASE_SCORE.getName())) {
                bonusCategoryScoreVo.setScore(comprehensiveScore.getBaseScore());
                bonusCategoryScoreVos.add(bonusCategoryScoreVo);
            } else if (Objects.equals(bonusCategory.getName(), BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getName())) {
                bonusCategoryScoreVo.setScore(comprehensiveScore.getPracticalInnovationScore());
                bonusCategoryScoreVos.add(bonusCategoryScoreVo);
            } else if (Objects.equals(bonusCategory.getName(), BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getName())) {
                // 得到惩戒分
                BigDecimal disciplineScore = comprehensiveScore.getDisciplineScore();
                bonusCategoryScoreVo.setScore(comprehensiveScore.getComprehensiveScore().add(disciplineScore));
                bonusCategoryScoreVos.add(bonusCategoryScoreVo);
            }
        }
        return bonusCategoryScoreVos;
    }

    /**
     * 根据不同类别获取学生信息条数
     *
     * @param yearId   学年id
     * @param category 类别
     * @param userId   学生id
     * @return 条数
     */
    @Override
    public Long getStuInfoRows(Integer yearId, Integer userId, String category) {
        // TODO 这里有魔法值，person、class、profession
        if (category.equalsIgnoreCase("person")) {
            return 1L;
        } else if (category.equalsIgnoreCase("class")) {
            // 拿到学生信息
            StuStudent stuStudent = stuStudentService.getById(userId);
            return this.count(new QueryWrapper<BonusComprehensiveScore>()
                    .eq("year_id", yearId)
                    .eq("class_id", stuStudent.getClassId())
                    .eq("college_id", stuStudent.getCollegeId())
                    .eq("grade_id", stuStudent.getGradeId())
                    .eq("profession_id", stuStudent.getProfessionId())
                    .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        } else if (category.equalsIgnoreCase("profession")) {
            // 拿到学生信息
            StuStudent stuStudent = stuStudentService.getById(userId);
            return this.count(new QueryWrapper<BonusComprehensiveScore>()
                    .eq("year_id", yearId)
                    .eq("college_id", stuStudent.getCollegeId())
                    .eq("grade_id", stuStudent.getGradeId())
                    .eq("profession_id", stuStudent.getProfessionId())
                    .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        }
        return 0L;
    }

    /**
     * 查询学生个人成绩信息
     *
     * @param userId 用户id
     * @param yearId 学年
     * @return 学生信息
     */
    @Override
    public StuScoreVo getStudentPersonScore(int userId, Integer yearId) {
        // 获取本人成绩
        BonusComprehensiveScore comprehensiveScore = this.getOne(new QueryWrapper<BonusComprehensiveScore>()
                .eq("stu_student_id", userId)
                .eq("year_id", yearId)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        if (comprehensiveScore == null) {
            throw new RuntimeException("不存在该学生的成绩信息!请联系管理员");
        }

        StuStudent stuStudent = stuStudentService.getById(userId);

        // 获取同专业的所有学生成绩
        List<BonusComprehensiveScore> comprehensiveScores = this.list(new QueryWrapper<BonusComprehensiveScore>()
                .eq("year_id", yearId)
                .eq("college_id", stuStudent.getCollegeId())
                .eq("grade_id", stuStudent.getGradeId())
                .eq("profession_id", stuStudent.getProfessionId())
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        // 收集学生id和score的map
        Map<Integer, BigDecimal> studentIdsAndScoreMap = comprehensiveScores.stream()
                .collect(Collectors.toMap(BonusComprehensiveScore::getStuStudentId, BonusComprehensiveScore::getScore));

        List<Map.Entry<Integer, BigDecimal>> sortScores = new ArrayList<>(studentIdsAndScoreMap.entrySet());
        //然后通过比较器来实现排序  -  降序排序
        Collections.sort(sortScores, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        StudentCoreInfo studentCoreInfo = asyncAndGetStudentCoreInfo(stuStudent, yearId);
        // 3.拿到加分项类别分数信息
        List<BonusCategory> bonusCategories = defaultCache.getBonusCategoriesCache();
        List<BonusCategoryScoreVo> bonusCategoryScoreVos = getBonusCategoryScoreVos(bonusCategories, stuStudent, yearId);

        // 4.设置学生成绩信息
        StuScoreVo stuScoreVo = setUpStuScoreVo(stuStudent, studentCoreInfo, bonusCategoryScoreVos);

        stuScoreVo.setStuScore(comprehensiveScore.getScore());
        // 设置排名
        for (int i = 0; i < sortScores.size(); i++) {
            Map.Entry<Integer, BigDecimal> decimalEntry = sortScores.get(i);
            if (decimalEntry.getKey() == userId) {
                stuScoreVo.setRank(i + 1);
                break;
            }
        }

        return stuScoreVo;
    }

    /**
     * 刷新学生成绩
     *
     * @param yearId 学年id
     */
    @Transactional
    @Override
    public void refreshStuScore(Integer yearId) {
        refreshStuScoreNew(yearId);
//        refreshStuScoreOld(yearId);
    }

    /**
     * 刷新学生成绩
     *
     * @param yearId 学年id
     */
    @Transactional
    public void refreshStuScoreOld(Integer yearId) {
        long startTime = System.currentTimeMillis();

        // 1.拿到所有学生id
        List<Integer> stuStudentIds = stuStudentMapper.getAllStudentIds();

        List<BonusCategory> categories = defaultCache.getBonusCategoriesCache();

        // 2.查询每个学生id的bonusApply
        // map : <学生id, 加分项类别ScoreMap<加分项类别id, 加分项类别分数>>
        Map<Integer, Map<Integer, BigDecimal>> studentIdsAndCategoryScoreVoMap = new HashMap<>();
        for (Integer stuStudentId : stuStudentIds) {
            // 只有approval为已审核的材料，才会进入到下面的逻辑（添加到comprehensive_score）
            List<BonusApply> bonusApplies = bonusApplyService.list(new QueryWrapper<BonusApply>()
                    .eq("stu_student_id", stuStudentId)
                    .eq("year_id", yearId)
                    .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

            // 3.根据每个加分申请表获取可以加分的分数
            Map<Integer, BigDecimal> categoryIdAndScoreMap = new HashMap<>();
            categoryIdAndScoreMap.put(BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode(),
                    BonusConstant.CategoryWeightEnum.BASE_SCORE.getBenchmarkScore());

            categoryIdAndScoreMap.put(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode(),
                    BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getBenchmarkScore());

            categoryIdAndScoreMap.put(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode(),
                    BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getBenchmarkScore());

            categoryIdAndScoreMap.put(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode(),
                    BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getBenchmarkScore());

            for (BonusApply bonusApply : bonusApplies) {
                if (bonusApply.getApproval() == BonusConstant.BonusApplyEnum.APPROVED.getCode()) {
                    for (BonusCategory category : categories) {
                        if (Objects.equals(category.getId(), bonusApply.getCategoryId())) {
                            BigDecimal score = categoryIdAndScoreMap.get(category.getId()) == null
                                    ? new BigDecimal("0.00")
                                    : categoryIdAndScoreMap.get(category.getId());
                            score = score.add(bonusApply.getScore()
                                    .multiply(category.getWeights())
                                    .multiply(BonusConstant.WEIGHT_RATIO)
                                    .setScale(2, RoundingMode.HALF_UP));
                            // 判断类别分数是否超过上限   对于超过上限的分数统一设置为upperLimit
                            if (Objects.equals(category.getId(), BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode())) {
                                if (BonusConstant.CategoryWeightEnum.BASE_SCORE.getGradeAndUpperLimitMap().get(null).compareTo(score) < 0) {
                                    score = BonusConstant.CategoryWeightEnum.BASE_SCORE.getGradeAndUpperLimitMap().get(null);
                                }
                            } else if (Objects.equals(category.getId(), BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode())) {
                                StuStudent stuStudent = stuStudentService.getById(stuStudentId);
                                String gradeName = defaultCache.getIdAndGradeNameMap().get(stuStudent.getGradeId());
                                if (BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getGradeAndUpperLimitMap().get(gradeName).compareTo(score) < 0) {
                                    score = BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getGradeAndUpperLimitMap().get(gradeName);
                                }
                            } else if (Objects.equals(category.getId(), BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode())) {
                                if (BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getGradeAndUpperLimitMap().get(null).compareTo(score) < 0) {
                                    score = BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getGradeAndUpperLimitMap().get(null);
                                }
                            }
                            categoryIdAndScoreMap.put(category.getId(), score);
                            break;
                        }
                    }
                }
            }

            studentIdsAndCategoryScoreVoMap.put(stuStudentId, categoryIdAndScoreMap);
        }

        // 4.更新comprehensive表
        studentIdsAndCategoryScoreVoMap.forEach((studentId, categoryScoreVo) -> {
            // 拿到每个类别的分数
            Map<Integer, BigDecimal> categoryScoreMap = studentIdsAndCategoryScoreVoMap.get(studentId);
            BigDecimal totalScore = new BigDecimal("0.00");
            BigDecimal baseScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode());
            BigDecimal practicalInnovationScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode());
            BigDecimal comprehensiveScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode());
            // 惩戒分数是负数
            BigDecimal disciplineScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode());
            // 计算总得分
            totalScore = totalScore.add(baseScore).add(comprehensiveScore).add(practicalInnovationScore).add(disciplineScore).setScale(2, RoundingMode.HALF_UP);

            this.update(new UpdateWrapper<BonusComprehensiveScore>()
                    .set("score", totalScore)
                    .set("base_score", baseScore)
                    .set("comprehensive_score", comprehensiveScore)
                    .set("discipline_score", disciplineScore)
                    .set("practical_innovation_score", practicalInnovationScore)
                    .eq("year_id", yearId)
                    .eq("stu_student_id", studentId));
        });

        // 删除对应的profession_year 、  class_year 、 学生端学生信息的条数  、 老师端的学生信息  、 老师端的row条数
        redisUtils.delete(RedisKeyConstant.TEACHER_SCORE_ALL_LIST_KEY);
        redisUtils.delete(RedisKeyConstant.TEACHER_SCORE_ROWS_KEY);
        redisUtils.delete(RedisKeyConstant.STUDENT_SCORE_YEAR_CLASS_LIST_KEY);
        redisUtils.delete(RedisKeyConstant.STUDENT_SCORE_YEAR_PROFESSION_LIST_KEY);
        redisUtils.delete(RedisKeyConstant.STUDENT_SCORE_ROWS_KEY);
        redisUtils.deleteSpecialPrefix(RedisKeyConstant.STUDENT_SCORE_PERSON_BY_YEAR_SINGLE_KEY + ":" + yearId);
        // 删除学生信息明细
        redisUtils.deleteSpecialPrefix(RedisKeyConstant.STUDENT_SCORE_DETAILS_BY_YEAR_CATEGORY_LIST_KEY);

        long endTime = System.currentTimeMillis();
        System.out.println("花费时间 --> " + (endTime - startTime) + "ms");
    }

    /**
     * 刷新学生成绩
     *
     * @param yearId 学年id
     */
    public void refreshStuScoreNew(Integer yearId) {
        long startTime = System.currentTimeMillis();

        // 1.拿到所有
        List<BonusComprehensiveScore> comprehensiveScoreList = this.list(new QueryWrapper<BonusComprehensiveScore>().eq("year_id", yearId));

        List<BonusCategory> categories = defaultCache.getBonusCategoriesCache();

        // 2.查询每个学生id的bonusApply
        // map : <学生id, 加分项类别ScoreMap<加分项类别id, 加分项类别分数>>
        Map<BonusComprehensiveScore, Map<Integer, BigDecimal>> bonusComprehensiveAndCategoryScoreVoMap = new HashMap<>();
        for (BonusComprehensiveScore bonusComprehensiveScore : comprehensiveScoreList) {
            // 只有approval为已审核的材料，才会进入到下面的逻辑（添加到comprehensive_score）
            List<BonusApply> bonusApplies = bonusApplyService.list(new QueryWrapper<BonusApply>()
                    .eq("stu_student_id", bonusComprehensiveScore.getStuStudentId())
                    .eq("year_id", yearId)
                    .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

            // 3.根据每个加分申请表获取可以加分的分数
            Map<Integer, BigDecimal> categoryIdAndScoreMap = new HashMap<>();

            // 拿到所有的加分类别信息
            Map<Integer, BonusConstant.CategoryWeightEnum> weightEnums = BonusUtils.getCategoryWeightEnums();
            weightEnums.forEach((key, val) -> {
                categoryIdAndScoreMap.put(key, val.getBenchmarkScore());
            });

            for (BonusApply bonusApply : bonusApplies) {
                if (bonusApply.getApproval() == BonusConstant.BonusApplyEnum.APPROVED.getCode()) {
                    for (BonusCategory category : categories) {
                        if (Objects.equals(category.getId(), bonusApply.getCategoryId())) {
                            BigDecimal score = categoryIdAndScoreMap.get(category.getId()) == null
                                    ? new BigDecimal("0.00")
                                    : categoryIdAndScoreMap.get(category.getId());
                            score = score.add(bonusApply.getScore()
                                    .multiply(category.getWeights())
                                    .multiply(BonusConstant.WEIGHT_RATIO)
                                    .setScale(2, RoundingMode.HALF_UP));
                            // 判断类别分数是否超过上限   对于超过上限的分数统一设置为upperLimit
                            if (Objects.equals(category.getId(), BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode())) {
                                if (BonusConstant.CategoryWeightEnum.BASE_SCORE.getGradeAndUpperLimitMap().get(null).compareTo(score) < 0) {
                                    score = BonusConstant.CategoryWeightEnum.BASE_SCORE.getGradeAndUpperLimitMap().get(null);
                                }
                            } else if (Objects.equals(category.getId(), BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode())) {
                                StuStudent stuStudent = stuStudentService.getById(bonusComprehensiveScore.getStuStudentId());
                                String gradeName = defaultCache.getIdAndGradeNameMap().get(stuStudent.getGradeId());
                                if (BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getGradeAndUpperLimitMap().get(gradeName).compareTo(score) < 0) {
                                    score = BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getGradeAndUpperLimitMap().get(gradeName);
                                }
                            } else if (Objects.equals(category.getId(), BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode())) {
                                if (BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getGradeAndUpperLimitMap().get(null).compareTo(score) < 0) {
                                    score = BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getGradeAndUpperLimitMap().get(null);
                                }
                            }
                            categoryIdAndScoreMap.put(category.getId(), score);
                            break;
                        }
                    }
                }
            }

            bonusComprehensiveAndCategoryScoreVoMap.put(bonusComprehensiveScore, categoryIdAndScoreMap);
        }

        // 4.更新comprehensive表
        bonusComprehensiveAndCategoryScoreVoMap.forEach((bonusComprehensiveScore, categoryScoreVo) -> {
            // 拿到每个类别的分数
            Map<Integer, BigDecimal> categoryScoreMap = bonusComprehensiveAndCategoryScoreVoMap.get(bonusComprehensiveScore);
            BigDecimal totalScore = new BigDecimal("0.00");
            BigDecimal baseScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode());
            BigDecimal practicalInnovationScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode());
            BigDecimal comprehensiveScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode());
            // 惩戒分数是负数
            BigDecimal disciplineScore = categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode()) == null
                    ? new BigDecimal("0.00")
                    : categoryScoreMap.get(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode());

            if (bonusComprehensiveScore.getBaseScore().compareTo(baseScore) != 0
                    || bonusComprehensiveScore.getComprehensiveScore().compareTo(comprehensiveScore) != 0
                    || bonusComprehensiveScore.getDisciplineScore().compareTo(disciplineScore) != 0
                    || bonusComprehensiveScore.getPracticalInnovationScore().compareTo(practicalInnovationScore) != 0) {
                // 计算总得分
                totalScore = totalScore.add(baseScore).add(comprehensiveScore).add(practicalInnovationScore).add(disciplineScore).setScale(2, RoundingMode.HALF_UP);
                this.update(new UpdateWrapper<BonusComprehensiveScore>()
                        .set("score", totalScore)
                        .set("base_score", baseScore)
                        .set("comprehensive_score", comprehensiveScore)
                        .set("discipline_score", disciplineScore)
                        .set("practical_innovation_score", practicalInnovationScore)
                        .eq("year_id", yearId)
                        .eq("stu_student_id", bonusComprehensiveScore.getStuStudentId()));

                // 采用先更后删策略
                // 删除对应的profession_year 、  class_year 、 学生端学生信息的条数  、 老师端的学生信息  、 老师端的row条数
                redisUtils.delete(RedisKeyConstant.TEACHER_SCORE_ALL_LIST_KEY);
                redisUtils.delete(RedisKeyConstant.TEACHER_SCORE_ROWS_KEY);
                redisUtils.delete(RedisKeyConstant.STUDENT_SCORE_YEAR_CLASS_LIST_KEY);
                redisUtils.delete(RedisKeyConstant.STUDENT_SCORE_YEAR_PROFESSION_LIST_KEY);
                redisUtils.delete(RedisKeyConstant.STUDENT_SCORE_ROWS_KEY);
                redisUtils.deleteSpecialPrefix(RedisKeyConstant.STUDENT_SCORE_PERSON_BY_YEAR_SINGLE_KEY + ":" + yearId);
                // 删除学生信息明细
                redisUtils.deleteSpecialPrefix(RedisKeyConstant.STUDENT_SCORE_DETAILS_BY_YEAR_CATEGORY_LIST_KEY);
            }
        });

        long endTime = System.currentTimeMillis();
        log.info("刷新学生成绩 花费时间 --> " + (endTime - startTime) + "ms");
    }

//    @Autowired
//    private SqlSessionTemplate sqlSessionTemplate;

//    @Transactional
//    public void transactionalControl(List<UpdateWrapper<BonusComprehensiveScore>> needUpdateWrapperList) throws SQLException {
//
//        //2、根据sqlSessionTemplate获取SqlSession工厂
//        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        //3、获取Connection来手动控制事务
//        Connection connection = sqlSession.getConnection();
//        try {
//            //4、设置手动提交
//            connection.setAutoCommit(false);
//            //7、将传入List中的10000个数据按1000一组均分成10组
//            List<List<UpdateWrapper<BonusComprehensiveScore>>> averageAssign =
//                    ListUtils.averageAssign(needUpdateWrapperList, needUpdateWrapperList.size() / refreshThreadPoolExecutor.getMaximumPoolSize());
//            //8、新建任务列表
//            List<Callable<Boolean>> callableList = new ArrayList<>();
//            //9、根据均分的5组数据分别新建5个Callable任务
//            for (int i = 0; i < averageAssign.size(); i++) {
//                List<UpdateWrapper<BonusComprehensiveScore>> updateList = averageAssign.get(i);
//                for (UpdateWrapper<BonusComprehensiveScore> bonusComprehensiveScoreUpdateWrapper : updateList) {
//                    Callable<Boolean> callable = () -> {
//                        boolean n = false;
//                        try {
//                            n = bonusComprehensiveScoreService.update(bonusComprehensiveScoreUpdateWrapper);
//                        } catch (Exception e) {
//                            //插入失败返回0
//                            return n;
//                        }
//                        //插入成功返回成功提交数
//                        return n;
//                    };
//                    callableList.add(callable);
//                }
//            }
//
//            //10、任务放入线程池开始执行
//            List<Future<Boolean>> futures = refreshThreadPoolExecutor.invokeAll(callableList);
//            //11、对比每个任务的返回值 <= 0 代表执行失败
//            for (Future<Boolean> future : futures) {
//                if (!future.get()) {
//                    //12、只要有一组任务失败回滚整个connection
//                    connection.rollback();
//                    return;
//                }
//            }
//            //13、主线程和子线程都执行成功 直接提交
//            connection.commit();
//            System.out.println("添加成功！");
//
//        } catch (Exception e) {
//            //14、主线程报错回滚
//            connection.rollback();
//            log.error(e.toString());
//            throw new SQLException("出现异常！");
//        }
//    }

    /**
     * 老师端获取学生信息条数
     *
     * @param yearId       学年id
     * @param professionId 专业id
     * @param gradeId      年级id
     * @return 条数
     */
    @Override
    public Long teacherGetStuInfoRows(Integer yearId, Integer professionId, Integer gradeId) {
        return this.count(new QueryWrapper<BonusComprehensiveScore>()
                .eq("year_id", yearId)
                .eq("profession_id", professionId)
                .eq("grade_id", gradeId)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
    }


    /**
     * 处理学生排名
     *
     * @param stuScoreVos 学生信息
     */
    private void dealStuScoreRank(List<StuScoreVo> stuScoreVos) {

        stuScoreVos.sort(((o1, o2) -> {
            return o2.getStuScore().compareTo(o1.getStuScore());
        }));

        for (int i = 0; i < stuScoreVos.size(); i++) {
            StuScoreVo stuScoreVo = stuScoreVos.get(i);
            stuScoreVo.setRank(i + 1);
        }
    }

    /**
     * 处理学生排名
     *
     * @param stuScoreVos 学生信息
     */
    private void dealStuScoreRank(List<StuScoreVo> stuScoreVos, Map<String, Object> params) {
        // 用于解决每页开始的都是从第1名开始
        int page = 1;
        int limit = 1;
        if (params.get(Constant.PAGE) != null && params.get(Constant.LIMIT) != null) {
            page = Integer.parseInt(String.valueOf(params.get(Constant.PAGE)));
            limit = Integer.parseInt(String.valueOf(params.get(Constant.LIMIT)));
        }

        stuScoreVos.sort(((o1, o2) -> {
            return o2.getStuScore().compareTo(o1.getStuScore());
        }));

        for (int i = 0; i < stuScoreVos.size(); i++) {
            StuScoreVo stuScoreVo = stuScoreVos.get(i);
            stuScoreVo.setRank((page - 1) * limit + i + 1);
        }
    }

    /**
     * 根据加分信息StuBonusVo算出最终得分
     *
     * @param stuBonusVo 学生加分项（内含加分项类别信息）
     * @return 总成绩
     */
    @Override
    public BigDecimal calculateScore(StuBonusVo stuBonusVo) {
        // 真正加分成绩已经不查bonus_bonus，而是查的bonus_apply的score
        if (stuBonusVo.getApproval() == BonusConstant.BonusApplyEnum.APPROVED.getCode()) {
            return stuBonusVo.getBonusScore();
        }
        if (stuBonusVo.getBonusScore() == null) {
            stuBonusVo.setBonusScore(new BigDecimal("0.00"));
        }

        return stuBonusVo.getBonusScore();
    }

}
