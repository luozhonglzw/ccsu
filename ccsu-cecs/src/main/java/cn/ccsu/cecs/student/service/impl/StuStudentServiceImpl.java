package cn.ccsu.cecs.student.service.impl;

import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.service.IBonusApplyService;
import cn.ccsu.cecs.bonus.vo.CategoryVo;
import cn.ccsu.cecs.bonus.vo.StuScoreDetailsVo;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.mapper.StuStudentMapper;
import cn.ccsu.cecs.student.service.*;
import cn.ccsu.cecs.student.utils.StuUtils;
import cn.ccsu.cecs.student.vo.StuStudentVo;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import cn.ccsu.cecs.student.vo.teacher.TeacherStudentVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Slf4j
@Service
public class StuStudentServiceImpl extends ServiceImpl<StuStudentMapper, StuStudent> implements IStuStudentService {

    @Autowired
    IStuStudentService stuStudentService;

    @Autowired
    IStuCollegeService collegeService;

    @Autowired
    IStuClassService classService;

    @Autowired
    IStuProfessionService professionService;

    @Autowired
    IStuGradeService gradeService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    DefaultCache defaultCache;

    @Autowired
    GlobalExecutor globalExecutor;

    @Autowired
    IBonusApplyService bonusApplyService;

    @Override
    public StuStudent getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StuStudent> page = this.page(
                new Query<StuStudent>().getPage(params),
                new QueryWrapper<StuStudent>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );

        // 获取学生分页信息
        List<StuStudent> stuStudents = page.getRecords();
        IPage<TeacherStudentVo> teacherStudentVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<TeacherStudentVo> teacherStudentVos = stuStudents.stream().map(stuStudent -> {
            TeacherStudentVo teacherStudentVo = new TeacherStudentVo();

            // 查询班级、专业、年级、学院信息
            StuStudentVo studentVo = getStudentVo(stuStudent.getId());
            teacherStudentVo.setId(stuStudent.getId());
            teacherStudentVo.setClassId(stuStudent.getClassId());
            teacherStudentVo.setClassName(studentVo.getClassName());
            teacherStudentVo.setProfessionId(stuStudent.getProfessionId());
            teacherStudentVo.setProfessionName(studentVo.getProfessionName());
            teacherStudentVo.setGradeId(stuStudent.getGradeId());
            teacherStudentVo.setGradeName(studentVo.getGradeName());
            teacherStudentVo.setCollegeId(stuStudent.getCollegeId());
            teacherStudentVo.setCollegeName(studentVo.getCollegeName());
            return teacherStudentVo;
        }).collect(Collectors.toList());

        teacherStudentVoPage.setRecords(teacherStudentVos);

        // 返回分页的TeacherStudentVo对象
        return new PageUtils(teacherStudentVoPage);
    }

    @Override
    public boolean login(StudentLoginVo studentLoginVo) {
        // 获取学号和密码
        String stuNumber = studentLoginVo.getStuNumber();
        String password = studentLoginVo.getPassword();

        // 检查学号格式
        StuUtils.checkStuNumber(stuNumber);

        // md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        StuStudent student = this.baseMapper.selectOne(new QueryWrapper<StuStudent>()
                .eq("stu_number", stuNumber)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        if (student == null) {
            throw new RuntimeException("学生信息不存在，请联系管理员");
        }
        if (!student.getPassword().equals(password)) {
            return false;
        }

        // 保存学生id、姓名
        studentLoginVo.setId(student.getId());
        studentLoginVo.setStuName(student.getStuName());
        return true;
    }

    @Override
    public void modifyPassword(StudentLoginVo studentLoginVo) {
        StuStudent stuStudent = new StuStudent();
        BeanUtils.copyProperties(studentLoginVo, stuStudent);
        stuStudent.setPassword(studentLoginVo.getPassword());
        // md5加密
        studentLoginVo.setPassword(DigestUtils.md5DigestAsHex(studentLoginVo.getPassword().getBytes()));

        this.update(stuStudent, new UpdateWrapper<StuStudent>()
                .set("password", studentLoginVo.getPassword())
                .set("updated_at", new Date())
                .set("updated_by", studentLoginVo.getStuName())
                .eq("stu_number", studentLoginVo.getStuNumber()));
    }

    @Override
    public StuStudentVo getStudentVo(Integer id) {
        // 1.查询学生学院id、专业id、班级id
        StuStudent student = stuStudentService.getById(id);

        StuStudentVo stuStudentVo = new StuStudentVo();
        stuStudentVo.setStuName(student.getStuName());
        stuStudentVo.setStuNumber(student.getStuNumber());

        // 从缓存中拿到所有信息
        String professionName = defaultCache.getIdAndProfessionNameMap().get(student.getProfessionId());
        stuStudentVo.setProfessionName(professionName);

        String gradeName = defaultCache.getIdAndGradeNameMap().get(student.getGradeId());
        stuStudentVo.setGradeName(gradeName);

        String className = defaultCache.getIdAndClassNameMap().get(student.getClassId());
        stuStudentVo.setClassName(className);

        String collegeName = defaultCache.getIdAndCollegeNameMap().get(student.getCollegeId());
        stuStudentVo.setCollegeName(collegeName);

        return stuStudentVo;
    }

    /**
     * 查询学生成绩明细（申请表组成）
     *
     * @return 响应
     */
    @Override
    public List<StuScoreDetailsVo> getStuScoreDetails(Integer yearId, Integer categoryId, int userId, Integer page, Integer limit) {
        page = (page - 1) * limit;

        long totalCount;
        List<StuScoreDetailsVo> stuScoreDetailsVos = new ArrayList<>();
        if (Objects.equals(categoryId, BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode())) {
            // 查询减分A2
            List<StuScoreDetailsVo> stuSubScoreDetailsVos = this.baseMapper.getStuScoreDetailsByCategoryId(yearId,
                    BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode(), userId, page, limit);
            stuScoreDetailsVos.addAll(stuSubScoreDetailsVos);

            // 查询总条数
            totalCount = bonusApplyService.count(new QueryWrapper<BonusApply>()
                    .eq("year_id", yearId)
                    .eq("stu_student_id", userId)
                    .eq("approval", BonusConstant.BonusApplyEnum.APPROVED.getCode())
                    .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
                    .in("category_id", Arrays.asList(BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode(), BonusConstant.CategoryWeightEnum.BASIC_QUALITY_SUB_SCORE.getCode())));
        } else {
            // 查询总条数
            totalCount = bonusApplyService.count(new QueryWrapper<BonusApply>()
                    .eq("year_id", yearId)
                    .eq("stu_student_id", userId)
                    .eq("approval", BonusConstant.BonusApplyEnum.APPROVED.getCode())
                    .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
                    .in("category_id", BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode()));
        }
        // 查询加分A1
        List<StuScoreDetailsVo> stuAddScoreDetailsVos =
                this.baseMapper.getStuScoreDetailsByCategoryId(yearId, categoryId, userId, page, limit);

        stuScoreDetailsVos.addAll(stuAddScoreDetailsVos);

        // 算出最终成绩（乘以权重）
        stuScoreDetailsVos.forEach(stuScoreDetailsVo -> {
            CategoryVo categoryVo = defaultCache.getCategoryVo(stuScoreDetailsVo.getCategoryId());
            stuScoreDetailsVo.setBonusScore(stuScoreDetailsVo.getBonusScore()
                    .multiply(BonusConstant.WEIGHT_RATIO)
                    .multiply(categoryVo.getWeight())
                    .setScale(2, RoundingMode.HALF_UP));
        });

        YearVo yearVo = defaultCache.getYearVo(yearId);
        stuScoreDetailsVos.forEach(stuScoreDetailsVo -> {
            stuScoreDetailsVo.setYearId(yearVo.getYearId());
            stuScoreDetailsVo.setYearName(yearVo.getYearName());

            CategoryVo categoryVo = defaultCache.getCategoryVo(stuScoreDetailsVo.getCategoryId());
            stuScoreDetailsVo.setCategoryVo(categoryVo);
            stuScoreDetailsVo.setTotalCount(totalCount);
        });

        return stuScoreDetailsVos;
    }
}
