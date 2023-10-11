package cn.ccsu.cecs.common.cache;

import cn.ccsu.cecs.bonus.entity.BonusCategory;
import cn.ccsu.cecs.bonus.vo.CategoryVo;
import cn.ccsu.cecs.bonus.vo.YearVo;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DefaultCache {

    /**
     * 存放学年信息  本地缓存
     */
    private List<YearVo> yearVoCache = new ArrayList<>();

    /**
     * 存放类别信息  本地缓存
     */
    private List<CategoryVo> categoryVoCache = new ArrayList<>();

    /**
     * 存放原始类别信息  本地缓存
     */
    private List<BonusCategory> bonusCategoriesCache = new ArrayList<>();

    /**
     * 存放classId和className  本地缓存
     */
    private Map<Integer, String> idAndClassNameMap = new HashMap<>();

    /**
     * 存放professionId和professionName  本地缓存
     */
    private Map<Integer, String> idAndProfessionNameMap = new HashMap<>();

    /**
     * 存放gradeId和gradeName  本地缓存
     */
    private Map<Integer, String> idAndGradeNameMap = new HashMap<>();

    /**
     * 存放collegeId和collegeName  本地缓存
     */
    private Map<Integer, String> idAndCollegeNameMap = new HashMap<>();

    public Map<Integer, String> getIdAndClassNameMap() {
        return idAndClassNameMap;
    }

    public void setIdAndClassNameMap(Map<Integer, String> idAndClassNameMap) {
        this.idAndClassNameMap = idAndClassNameMap;
    }

    public Map<Integer, String> getIdAndProfessionNameMap() {
        return idAndProfessionNameMap;
    }

    public void setIdAndProfessionNameMap(Map<Integer, String> idAndProfessionNameMap) {
        this.idAndProfessionNameMap = idAndProfessionNameMap;
    }

    public Map<Integer, String> getIdAndGradeNameMap() {

        return idAndGradeNameMap;
    }

    public void setIdAndGradeNameMap(Map<Integer, String> idAndGradeNameMap) {
        this.idAndGradeNameMap = idAndGradeNameMap;
    }

    public Map<Integer, String> getIdAndCollegeNameMap() {
        return idAndCollegeNameMap;
    }

    public void setIdAndCollegeNameMap(Map<Integer, String> idAndCollegeNameMap) {
        this.idAndCollegeNameMap = idAndCollegeNameMap;
    }

    public YearVo getYearVo(Integer yearId) {
        for (YearVo yearVo : yearVoCache) {
            if (Objects.equals(yearVo.getYearId(), yearId)) {
                return yearVo;
            }
        }
        return null;
    }

    public CategoryVo getCategoryVo(Integer categoryId) {
        for (CategoryVo categoryVo : categoryVoCache) {
            if (Objects.equals(categoryVo.getId(), categoryId)) {
                return categoryVo;
            }
        }
        return null;
    }

    public List<CategoryVo> getCategoryVoCache() {
        return categoryVoCache;
    }

    public List<YearVo> getYearVoCache() {
        return yearVoCache;
    }

    public void setYearVoCache(List<YearVo> yearVoCache) {
        this.yearVoCache = yearVoCache;
    }

    public void setCategoryVoCache(List<CategoryVo> categoryVoCache) {
        this.categoryVoCache = categoryVoCache;
    }

    public List<BonusCategory> getBonusCategoriesCache() {
        return bonusCategoriesCache;
    }

    public void setBonusCategoriesCache(List<BonusCategory> bonusCategoriesCache) {
        this.bonusCategoriesCache = bonusCategoriesCache;
    }
}
