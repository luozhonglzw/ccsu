package cn.ccsu.cecs.common.extend;

import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.service.IBonusCategoryService;
import cn.ccsu.cecs.bonus.service.IBonusYearService;
import cn.ccsu.cecs.bonus.vo.CategoryVo;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.student.entity.StuClass;
import cn.ccsu.cecs.student.entity.StuCollege;
import cn.ccsu.cecs.student.entity.StuGrade;
import cn.ccsu.cecs.student.entity.StuProfession;
import cn.ccsu.cecs.student.service.IStuClassService;
import cn.ccsu.cecs.student.service.IStuCollegeService;
import cn.ccsu.cecs.student.service.IStuGradeService;
import cn.ccsu.cecs.student.service.IStuProfessionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目启动的时候  执行afterPropertiesSet（）方法，将部分信息添加到本地缓存中
 */
@Service
public class InitializeCache implements InitializingBean {

    @Autowired
    IBonusYearService bonusYearService;

    @Autowired
    IBonusCategoryService bonusCategoryService;

    @Autowired
    IStuCollegeService stuCollegeService;

    @Autowired
    IStuGradeService stuGradeService;

    @Autowired
    IStuProfessionService stuProfessionService;

    @Autowired
    IStuClassService stuClassService;

    @Autowired
    DefaultCache defaultCache;

    @Override
    public void afterPropertiesSet() {
        // 1.将加分类别进行缓存
        List<BonusCategory> bonusCategories = bonusCategoryService.list();

        List<CategoryVo> categoryVos = new ArrayList<>();
        for (BonusCategory bonusCategory : bonusCategories) {
            CategoryVo categoryVo = new CategoryVo();
            categoryVo.setId(bonusCategory.getId());
            categoryVo.setName(bonusCategory.getName());
            categoryVo.setWeight(bonusCategory.getWeights());

            categoryVos.add(categoryVo);
        }

        // 2.将学年信息进行缓存
        List<YearVo> allYear = bonusYearService.getAllYear();

        defaultCache.setCategoryVoCache(categoryVos);
        defaultCache.setBonusCategoriesCache(bonusCategories);
        defaultCache.setYearVoCache(allYear);

        // 3.将学院信息进行缓存
        List<StuCollege> stuColleges = stuCollegeService.list();
        Map<Integer, String> stuCollegesMap = stuColleges.stream().collect(Collectors.toMap(StuCollege::getId, StuCollege::getName));
        defaultCache.setIdAndCollegeNameMap(stuCollegesMap);

        // 4.将年级信息进行缓存
        List<StuGrade> stuGrades = stuGradeService.list();
        Map<Integer, String> stuGradesMap = stuGrades.stream().collect(Collectors.toMap(StuGrade::getId, StuGrade::getGdName));
        defaultCache.setIdAndGradeNameMap(stuGradesMap);

        // 5.将专业信息进行缓存
        List<StuProfession> stuProfessions = stuProfessionService.list();
        Map<Integer, String> stuProfessionsMap = stuProfessions.stream().collect(Collectors.toMap(StuProfession::getId, StuProfession::getPfName));
        defaultCache.setIdAndProfessionNameMap(stuProfessionsMap);

        // 6.将班级信息进行缓存
        List<StuClass> stuClasses = stuClassService.list();
        Map<Integer, String> stuClasssMap = stuClasses.stream().collect(Collectors.toMap(StuClass::getId, StuClass::getClName));
        defaultCache.setIdAndClassNameMap(stuClasssMap);
    }
}
