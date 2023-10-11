package cn.ccsu.cecs.student.mapper;

import cn.ccsu.cecs.bonus.vo.StuScoreDetailsVo;
import cn.ccsu.cecs.common.entity.StudentCoreInfo;
import cn.ccsu.cecs.student.entity.StuStudent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 学生表 Mapper 接口
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Mapper
@Repository
public interface StuStudentMapper extends BaseMapper<StuStudent> {

    StudentCoreInfo getStudentCoreInfo(@Param("stu_student_id") Integer studentId);

    /**
     * 查询所有学生的id的list
     *
     * @return
     */
    List<Integer> getAllStudentIds();

    /**
     * 根据学年、学生id查询学生加分项细节信息
     *
     * @param yearId 学年id
     * @param userId 用户id
     * @return 学生加分信息细节
     */
    List<StuScoreDetailsVo> getStuScoreDetails(@Param("yearId") Integer yearId, @Param("userId") int userId);

    /**
     * 根据学年id、类别id、学生id查询学生加分项细节信息
     *
     * @param yearId 学年id
     * @param userId 用户id
     * @return 学生加分信息细节
     */
    List<StuScoreDetailsVo> getStuScoreDetailsByCategoryId(@Param("yearId") Integer yearId,
                                                           @Param("categoryId") Integer categoryId,
                                                           @Param("userId") int userId,
                                                           @Param("page") Integer page,
                                                           @Param("limit") Integer limit);

    /**
     * 根据学年id、类别id、学生id查询总条数
     *
     * @param yearId 学年id
     * @param userId 用户id
     * @return 总条数
     */
    Integer getStuScoreDetailsCount(@Param("yearId") Integer yearId,
                                    @Param("categoryId") Integer categoryId,
                                    @Param("userId") int userId);
}
