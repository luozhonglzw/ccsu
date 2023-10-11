package cn.ccsu.cecs.admin.service.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.ccsu.cecs.admin.service.AdminImportService;
import cn.ccsu.cecs.admin.service.AdminUserService;
import cn.ccsu.cecs.admin.vo.ImportStuScoreVo;
import cn.ccsu.cecs.admin.vo.excel.ExcelStuBaseScore;
import cn.ccsu.cecs.admin.vo.excel.ExcelStuInfo;
import cn.ccsu.cecs.admin.vo.excel.ExcelStuScore;
import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.entity.BonusBonus;
import cn.ccsu.cecs.bonus.entity.BonusComprehensiveScore;
import cn.ccsu.cecs.bonus.service.IBonusApplyService;
import cn.ccsu.cecs.bonus.service.IBonusBonusService;
import cn.ccsu.cecs.bonus.service.IBonusComprehensiveScoreService;
import cn.ccsu.cecs.bonus.service.IBonusYearService;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.entity.BaseInfo;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.RedisUtils;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.mapper.StuStudentMapper;
import cn.ccsu.cecs.student.service.IStuClassService;
import cn.ccsu.cecs.student.service.IStuCollegeService;
import cn.ccsu.cecs.student.service.IStuGradeService;
import cn.ccsu.cecs.student.service.IStuStudentService;
import cn.ccsu.cecs.student.utils.StuUtils;
import cn.ccsu.cecs.student.vo.ClassVo;
import cn.ccsu.cecs.student.vo.CollegeVo;
import cn.ccsu.cecs.student.vo.GradeVo;
import cn.ccsu.cecs.student.vo.ProfessionVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class AdminImportServiceImpl implements AdminImportService {

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    IStuClassService stuClassService;

    @Autowired
    IStuGradeService stuGradeService;

    @Autowired
    IStuStudentService stuStudentService;

    @Autowired
    StuStudentMapper stuStudentMapper;

    @Autowired
    IStuCollegeService stuCollegeService;

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    IBonusBonusService bonusBonusService;

    @Autowired
    IBonusApplyService bonusApplyService;

    @Autowired
    IBonusYearService bonusYearService;

    @Autowired
    IBonusComprehensiveScoreService bonusComprehensiveScoreService;

    @Autowired
    GlobalExecutor globalExecutor;

    @Autowired
    DefaultCache defaultCache;

    @Autowired
    RedisUtils redisUtils;

    @Transactional
    @Override
    public void importStudentInfo(MultipartFile file, HttpServletRequest request) throws Exception {
        String teacherName = JWTUtils.getPayload(request, "name");

        // 1.解析学生信息文件
        ImportParams params = new ImportParams();   // 没有标头，params采用默认值
        // easypoi解析，拿到学生数据
        List<ExcelStuInfo> excelStuInfos = ExcelImportUtil.importExcel(file.getInputStream(), ExcelStuInfo.class, params);

        // 查询所有的学院、专业、年级、班级信息
        BaseInfo baseInfo = adminUserService.getBaseInfo();

        // 2.循环判断是否专业、学院是否存在 + 学号是否有误
        for (int i = 0; i < excelStuInfos.size(); i++) {
            ExcelStuInfo excelStuInfo = excelStuInfos.get(i);
            // 先进行为null判断
            if (excelStuInfo.getStuNumber() == null || Objects.equals(excelStuInfo.getStuNumber(), ""))
                throw new RuntimeException("第" + (i + 2) + "行的学号信息为空！请检查或联系管理员");
            if (excelStuInfo.getClassName() == null || Objects.equals(excelStuInfo.getClassName(), ""))
                throw new RuntimeException("第" + (i + 2) + "行的班级信息为空！请检查或联系管理员");
            if (excelStuInfo.getCollegeName() == null || Objects.equals(excelStuInfo.getCollegeName(), ""))
                throw new RuntimeException("第" + (i + 2) + "行的学院信息为空！请检查或联系管理员");
            if (excelStuInfo.getGradeName() == null || Objects.equals(excelStuInfo.getGradeName(), ""))
                throw new RuntimeException("第" + (i + 2) + "行的年级信息为空！请检查或联系管理员");
            if (excelStuInfo.getProfessionName() == null || Objects.equals(excelStuInfo.getProfessionName(), ""))
                throw new RuntimeException("第" + (i + 2) + "行的专业信息为空！请检查或联系管理员");
            if (excelStuInfo.getStuName() == null || Objects.equals(excelStuInfo.getStuName(), ""))
                throw new RuntimeException("第" + (i + 2) + "行的姓名信息为空！请检查或联系管理员");

            // 判断学院
            List<CollegeVo> collegeVos = baseInfo.getCollegeVos();
            boolean collegeFlag = false;
            for (CollegeVo collegeVo : collegeVos) {
                if (collegeVo.getCollegeName().equalsIgnoreCase(excelStuInfo.getCollegeName())) {
                    excelStuInfo.setCollegeId(collegeVo.getId());
                    collegeFlag = true;
                }
            }
            if (!collegeFlag) {
                throw new RuntimeException("第" + (i + 2) + "行的学院信息有误！请检查或联系管理员");
            }

            // 判断年级
            List<GradeVo> gradeVos = baseInfo.getGradeVos();
            boolean gradeFlag = false;
            for (GradeVo gradeVo : gradeVos) {
                if (gradeVo.getGradeName().equalsIgnoreCase(excelStuInfo.getGradeName())) {
                    excelStuInfo.setGradeId(gradeVo.getId());
                    gradeFlag = true;
                }
            }
            if (!gradeFlag) {
                throw new RuntimeException("第" + (i + 2) + "行的年级信息有误！请检查或联系管理员");
            }

            // 判断专业
            List<ProfessionVo> professionVos = baseInfo.getProfessionVos();
            boolean professionFlag = false;
            for (ProfessionVo professionVo : professionVos) {
                if (professionVo.getProfessionName().equalsIgnoreCase(excelStuInfo.getProfessionName())) {
                    excelStuInfo.setProfessionId(professionVo.getId());
                    professionFlag = true;
                }
            }
            if (!professionFlag) {
                throw new RuntimeException("第" + (i + 2) + "行的专业信息有误！请检查或联系管理员");
            }

            // 判断班级
            List<ClassVo> classVos = baseInfo.getClassVos();
            boolean classFlag = false;
            for (ClassVo classVo : classVos) {
                if (classVo.getClassName().equalsIgnoreCase(excelStuInfo.getClassName())) {
                    excelStuInfo.setClassId(classVo.getId());
                    classFlag = true;
                }
            }
            if (!classFlag) {
                throw new RuntimeException("第" + (i + 2) + "行的班级信息有误！请检查或联系管理员");
            }

            // 判断学号
            try {
                StuUtils.checkStuNumber(excelStuInfo.getStuNumber());
            } catch (Exception e) {
                throw new RuntimeException("第" + (i + 2) + "行的学号信息有误！请检查或联系管理员");
            }

            // 判断学号是否已存在
            StuStudent student = stuStudentService.getOne(new QueryWrapper<StuStudent>()
                    .eq("stu_number", excelStuInfo.getStuNumber()));
            if (student != null) {
                throw new RuntimeException("第" + (i + 2) + "行的学号信息已存在！请检查或联系管理员");
            }
        }

        List<YearVo> allYear = bonusYearService.getAllYear();

        // 3.如果正确，批量插入
        excelStuInfos.forEach(excelStuInfo -> {
            // 先插入学生表
            String encryptPassword = DigestUtils.md5DigestAsHex(ProjectConstant.STUDENT_DEFAULT_PASSWORD.getBytes());
            StuStudent student = new StuStudent(null,
                    excelStuInfo.getCollegeId(),
                    excelStuInfo.getGradeId(),
                    excelStuInfo.getProfessionId(),
                    excelStuInfo.getClassId(),
                    excelStuInfo.getStuName(),
                    excelStuInfo.getStuNumber(),
                    encryptPassword,
                    new Date(), teacherName, null, null,
                    ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
            stuStudentMapper.insert(student);

            // 后给每个学年都插入bonus_comprehensive
            for (YearVo cacheYearVo : allYear) {
                BonusComprehensiveScore bonusComprehensiveScore = new BonusComprehensiveScore(
                        null,
                        cacheYearVo.getYearId(),
                        excelStuInfo.getCollegeId(),
                        excelStuInfo.getGradeId(),
                        excelStuInfo.getProfessionId(),
                        excelStuInfo.getClassId(),
                        student.getId(),
                        new BigDecimal("0.00"), new BigDecimal("0.00"),
                        new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"),
                        LocalDateTime.now(),
                        teacherName, null, null,
                        ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());
                bonusComprehensiveScoreService.save(bonusComprehensiveScore);
                log.info("学生成绩导入成功 student: " + student + " -> score: + " + bonusComprehensiveScore);
            }
        });
    }

    @Transactional
    @Override
    public void importStudentScore(ImportStuScoreVo importStuScoreVo, HttpServletRequest request) throws Exception {
        String teacherName = JWTUtils.getPayload(request, "name");
        // 判断是否为惩戒分数
        boolean isDiscipline = Objects.equals(importStuScoreVo.getCategoryId(), BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode());

        // 1.解析学生成绩文件
        ImportParams params = new ImportParams();   // 没有标头，params采用默认值
        // easypoi解析，拿到学生成绩数据
        List<ExcelStuScore> excelStuScores = ExcelImportUtil.importExcel(importStuScoreVo.getFile().getInputStream(), ExcelStuScore.class, params);

        Map<StuStudent, ExcelStuScore> stuInfoAndExcelStuScoreMap = new HashMap<>();
        Map<Integer, ExcelStuScore> studentIdsAndExcelStuScoreMap = new HashMap<>();
        // 检查学号
        for (int i = 0; i < excelStuScores.size(); i++) {
            ExcelStuScore excelStuScore = excelStuScores.get(i);

            if (excelStuScore == null || excelStuScore.getStuNumber() == null) {
                throw new RuntimeException("第" + (i + 2) + "行的学号信息为空！请检查文件或联系管理员");
            }

            if (isDiscipline) {
                if (excelStuScore.getBonusScore().compareTo(new BigDecimal("0.00")) > 0) {
                    excelStuScore.setBonusScore(new BigDecimal("-" + excelStuScore.getBonusScore().toString()));
                }
            }
            try {
                StuUtils.checkStuNumber(excelStuScore.getStuNumber());
            } catch (Exception e) {
                throw new RuntimeException("第" + (i + 2) + "行的学号信息有误！请检查文件或联系管理员");
            }

            StuStudent student = stuStudentService.getOne(new QueryWrapper<StuStudent>().eq("stu_number", excelStuScore.getStuNumber()));
            if (student == null) {
                throw new RuntimeException("第" + (i + 2) + "行的学号 [" + excelStuScore.getStuNumber() + "] 不存在，请检查文件!");
            }

            // 校验bonus_comprehensive_score是否存在
            BonusComprehensiveScore bonusComprehensiveScore = bonusComprehensiveScoreService.getOne(new QueryWrapper<BonusComprehensiveScore>()
                    .eq("year_id", importStuScoreVo.getYearId())
                    .eq("stu_student_id", student.getId()));
            if (bonusComprehensiveScore == null) {
                bonusComprehensiveScoreService.save(new BonusComprehensiveScore(
                        null,
                        importStuScoreVo.getYearId(),
                        student.getCollegeId(),
                        student.getGradeId(),
                        student.getProfessionId(),
                        student.getClassId(),
                        student.getId(),
                        new BigDecimal("0.00"), new BigDecimal("0.00"),
                        new BigDecimal("0.00"), new BigDecimal("0.00"), new BigDecimal("0.00"),
                        LocalDateTime.now(),
                        teacherName,
                        null,
                        null,
                        ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
            }
            stuInfoAndExcelStuScoreMap.put(student, excelStuScore);
            studentIdsAndExcelStuScoreMap.put(student.getId(), excelStuScore);
        }

        BonusBonus bonusBonus = bonusBonusService.getOne(new QueryWrapper<BonusBonus>()
                .eq("category_id", importStuScoreVo.getCategoryId()));

        // 保存bonus_apply
        List<BonusApply> bonusApplies = new ArrayList<>();
        studentIdsAndExcelStuScoreMap.forEach((studentId, excelStuScore) -> {
            BonusApply bonusApply = new BonusApply();
            bonusApply.setYearId(importStuScoreVo.getYearId());
            bonusApply.setCategoryId(importStuScoreVo.getCategoryId());
            bonusApply.setBonusId(bonusBonus.getId());
            bonusApply.setStuStudentId(studentId);
            bonusApply.setScore(excelStuScore.getBonusScore());
            bonusApply.setApproval(BonusConstant.BonusApplyEnum.APPROVED.getCode());
            bonusApply.setRemark(excelStuScore.getActivityName() == null ? excelStuScore.getRemark() : excelStuScore.getActivityName());
            bonusApply.setApprovalAt(new Date());
            bonusApply.setApprovalBy(teacherName);
            bonusApply.setApprovalComments(ProjectConstant.UNITE_APPROVAL_COMMENTS);
            bonusApply.setCreatedAt(new Date());
            bonusApply.setCreatedBy(teacherName);
            bonusApply.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());

            bonusApplies.add(bonusApply);
            log.info("管理员信息:" + TeacherInterceptor.teacherInfoThreadLocal.get() + " -> 导入综测信息:bonusApply:" + bonusApply);
        });

        // 批量插入
        bonusApplyService.saveBatch(bonusApplies);

        // 删除加分申请表的缓存
        redisUtils.deleteSpecialPrefix(RedisKeyConstant.STUDENT_BONUS_APPLY_LIST_SINGLE_KEY);
    }

    @Override
    public void importStudentBaseScore(ImportStuScoreVo importStuScoreVo, HttpServletRequest request) throws Exception {
        String teacherName = JWTUtils.getPayload(request, "name");

        // 1.解析学生成绩文件
        ImportParams params = new ImportParams();   // 没有标头，params采用默认值
        // easypoi解析，拿到学生成绩数据
        List<ExcelStuBaseScore> excelStuBaseScores = ExcelImportUtil.importExcel(importStuScoreVo.getFile().getInputStream(), ExcelStuBaseScore.class, params);

        Map<String, ExcelStuBaseScore> stuNumberAndExcelStuBaseScoreMap = new HashMap<>();
        // 检查学号
        for (int i = 0; i < excelStuBaseScores.size(); i++) {
            ExcelStuBaseScore excelStuBaseScore = excelStuBaseScores.get(i);

            if (excelStuBaseScore == null || excelStuBaseScore.getBaseScore() == null) {
                throw new RuntimeException("第" + (i + 2) + "行的专业成绩为空！请检查或联系管理员");
            }

            StuStudent student = stuStudentService.getOne(new QueryWrapper<StuStudent>().eq("stu_number", excelStuBaseScore.getStuNumber()));
            if (student == null) {
                throw new RuntimeException("第" + (i + 2) + "行的学号不存在，无法导入！请检查或联系管理员");
            }

            try {
                StuUtils.checkStuNumber(excelStuBaseScore.getStuNumber());
            } catch (Exception e) {
                throw new RuntimeException("第" + (i + 2) + "行的学号信息有误！请检查或联系管理员");
            }
            stuNumberAndExcelStuBaseScoreMap.put(excelStuBaseScore.getStuNumber(), excelStuBaseScores.get(i));
        }

        // 这个学生ids
        Map<Integer, ExcelStuBaseScore> studentIdsAndExcelStuBaseScoreMap = new HashMap<>();
        stuNumberAndExcelStuBaseScoreMap.forEach((stuNumber, excelStuBaseScore) -> {
            StuStudent student = stuStudentService.getOne(new QueryWrapper<StuStudent>().eq("stu_number", stuNumber));
            if (student != null) {
                studentIdsAndExcelStuBaseScoreMap.put(student.getId(), excelStuBaseScore);
            }
        });

        BonusBonus bonusBonus = bonusBonusService.getOne(new QueryWrapper<BonusBonus>()
                .eq("category_id", importStuScoreVo.getCategoryId()));

        // 保存bonus_apply
        List<String> redisKeyStudentIds = new ArrayList<>();
        List<BonusApply> bonusApplies = new ArrayList<>();
        studentIdsAndExcelStuBaseScoreMap.forEach((studentId, excelStuBaseScore) -> {
            BonusApply bonusApply = new BonusApply();
            bonusApply.setYearId(importStuScoreVo.getYearId());
            bonusApply.setCategoryId(importStuScoreVo.getCategoryId());
            bonusApply.setBonusId(bonusBonus.getId());
            bonusApply.setStuStudentId(studentId);
            bonusApply.setScore(excelStuBaseScore.getBaseScore());
            bonusApply.setApproval(BonusConstant.BonusApplyEnum.APPROVED.getCode());
            bonusApply.setRemark(bonusBonus.getName());
            bonusApply.setApprovalAt(new Date());
            bonusApply.setApprovalBy(teacherName);
            bonusApply.setApprovalComments(ProjectConstant.UNITE_APPROVAL_COMMENTS);
            bonusApply.setCreatedAt(new Date());
            bonusApply.setCreatedBy(teacherName);
            bonusApply.setDeleted(ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode());

            bonusApplies.add(bonusApply);
            // 用于统一删除指定学生id的信息
            redisKeyStudentIds.add(RedisKeyConstant.STUDENT_BONUS_APPLY_LIST_SINGLE_KEY + ":" + studentId);
            log.info("导入学生基本成绩 studentId:" + studentId + " -> bonusApply:" + bonusApply);
        });

        // 删除加分申请表的缓存
        redisUtils.batchDelete(redisKeyStudentIds);

        // 批量插入
        bonusApplyService.saveBatch(bonusApplies);
    }
}
