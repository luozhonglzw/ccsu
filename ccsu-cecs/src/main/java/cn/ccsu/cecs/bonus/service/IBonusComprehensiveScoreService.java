package cn.ccsu.cecs.bonus.service;

import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.entity.BonusComprehensiveScore;
import cn.ccsu.cecs.bonus.vo.StuBonusVo;
import cn.ccsu.cecs.bonus.vo.StuScoreVo;
import cn.ccsu.cecs.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 综合成绩表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-10
 */
public interface IBonusComprehensiveScoreService extends IService<BonusComprehensiveScore> {

    /**
     * 分页查询数据
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询学生所在班级的所有学生成绩（通过学年id）
     *
     * @param userId 学生id
     * @param yearId 学年id
     * @return 学生班级信息
     */
    List<StuScoreVo> getStuClassTotalScoreByYear(Integer userId, Integer yearId, Map<String, Object> params);

    /**
     * 查询学生所在班级的所有学生成绩（通过学年id）  -- 支持分页
     *
     * @param userId 学生id
     * @param yearId 学年id
     * @param params 分页参数
     * @return 学生班级信息
     */
    List<StuScoreVo> getStuClassTotalScoreByYearPage(Integer userId, Integer yearId, Map<String, Object> params);

    /**
     * 异步编排获取加分项信息
     *
     * @param bonusApplies 加分表list
     * @return 学生加分表的所有信息
     */
    List<StuBonusVo> getStuBonusVos(List<BonusApply> bonusApplies);

    /**
     * 根据加分信息StuBonusVo算出最终得分
     *
     * @param stuBonusVo 学生加分表信息
     */
    BigDecimal calculateScore(StuBonusVo stuBonusVo);

    /**
     * 查询学生所在班级的所有学生成绩（通过学年id）  --  同时支持分页
     *
     * @param userId 学生id
     * @param yearId 学年id
     * @return 结果
     */
    List<StuScoreVo> getStuProfessionTotalScoreByYearPage(Integer userId, Integer yearId, Map<String, Object> params);

    /**
     * 查询指定学年的学生所在年级、专业的成绩信息
     *
     * @param yearId       学年id
     * @param gradeId      年级id
     * @param professionId 专业id
     * @return 结果
     */
    List<StuScoreVo> getStudentScore(Integer yearId, Integer gradeId, Integer professionId, Map<String, Object> params);

    /**
     * 根据不同类别获取学生信息条数
     *
     * @param yearId   学年id
     * @param category 类别
     * @param userId   学生id
     * @return 条数
     */
    Long getStuInfoRows(Integer yearId, Integer userId, String category);


    /**
     * 查询学生个人成绩信息
     *
     * @param userId 用户id
     * @param yearId 学年
     * @return 学生信息
     */
    StuScoreVo getStudentPersonScore(int userId, Integer yearId);

    /**
     * 刷新学生成绩
     *
     * @param yearId 学年id
     */
    void refreshStuScore(Integer yearId);

    /**
     * 老师端获取学生信息条数
     *
     * @param yearId       学年id
     * @param professionId 专业id
     * @param gradeId      年级id
     * @return 条数
     */
    Long teacherGetStuInfoRows(Integer yearId, Integer professionId, Integer gradeId);
}
