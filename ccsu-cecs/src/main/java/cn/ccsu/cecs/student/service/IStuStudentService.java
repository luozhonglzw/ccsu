package cn.ccsu.cecs.student.service;

import cn.ccsu.cecs.bonus.vo.StuScoreDetailsVo;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.vo.StuStudentVo;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IStuStudentService extends IService<StuStudent> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 学生登录
     *
     * @param studentLoginVo 学生新
     * @return 结果
     */
    boolean login(StudentLoginVo studentLoginVo);

    /**
     * 学生修改密码
     *
     * @param studentLoginVo 学生信息
     */
    void modifyPassword(StudentLoginVo studentLoginVo);

    /**
     * 获取学生信息
     *
     * @param id 学生id
     * @return 学生信息
     */
    StuStudentVo getStudentVo(Integer id);

    /**
     * 查询学生加分类别明细，根据学年、类别、学号、分页
     *
     * @param yearId     学年
     * @param categoryId 类别
     * @param userId     用户
     * @param page       页码
     * @param limit      条数
     * @return 学生加分明细
     */
    List<StuScoreDetailsVo> getStuScoreDetails(Integer yearId, Integer categoryId, int userId, Integer page, Integer limit);
}
