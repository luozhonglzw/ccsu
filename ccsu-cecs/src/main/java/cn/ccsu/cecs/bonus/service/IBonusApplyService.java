package cn.ccsu.cecs.bonus.service;

import cn.ccsu.cecs.bonus.entity.BonusApply;
import cn.ccsu.cecs.bonus.vo.ReceiveBonusApplyVo;
import cn.ccsu.cecs.bonus.vo.StuBonusApplyVo;
import cn.ccsu.cecs.bonus.vo.teacher.BonusApplyVo;
import cn.ccsu.cecs.bonus.vo.teacher.ModifyBonusApplyVo;
import cn.ccsu.cecs.bonus.vo.teacher.VerifyVo;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 加分申请表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IBonusApplyService extends IService<BonusApply> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 持久化加分申请表
     *
     * @param user                用户信息
     * @param receiveBonusApplyVo 加分申请表
     */
    void submitApplyInfo(StudentLoginVo user, ReceiveBonusApplyVo receiveBonusApplyVo);

    /**
     * 查询用户指定学年的加分申请表信息
     *
     * @param yearId 学年id
     * @param userId 用户信息id
     * @return 加分申请表信息
     */
    StuBonusApplyVo getStuBonusApplyVoByYearId(Integer yearId, Integer userId);

    /**
     * 删除申请表id
     *
     * @param bonusApplyId 申请表id
     * @param updateName   修改者名字
     */
    void deleteBonusApply(Integer bonusApplyId, String updateName);

    /**
     * 查询加分申请表
     *
     * @param id 加分申请表id
     * @return 前端加分申请表Vo对象
     */
    BonusApplyVo getBonusApplyVo(Integer id);

    /**
     * 获取加分申请表（条件：学年、年级、专业、审核状态）
     *
     * @param yearId       学年id
     * @param gradeId      年级id
     * @param professionId 专业id
     * @param approval     审核状态
     * @return 需要申请的加分申请表
     */
    List<BonusApplyVo> getApprovalInfo(Integer yearId, Integer gradeId, Integer professionId, Integer approval);

    /**
     * 管理员审核加分申请表
     *
     * @param name     审核者姓名
     * @param verifyVo 审核对象Vo
     */
    void verify(String name, VerifyVo verifyVo);

    /**
     * 老师端 - 修改加分申请表
     *
     * @param modifyBonusApplyVo 前端加分申请表Vo对象
     * @param teacherName        老师名称
     */
    void modifyBonusApplyWithTeacher(ModifyBonusApplyVo modifyBonusApplyVo, String teacherName);

    /**
     * 查询用户指定的加分申请表id的信息
     *
     * @param bonusApplyId 加分申请表id
     * @return 加分申请表信息
     */
    BonusApplyVo getBonusApplyVoByBonusApplyId(Integer bonusApplyId);

    /**
     * 查询用户所有学年的加分申请表信息
     *
     * @param userId 用户id
     * @return 所有加分申请表信息
     */
    List<StuBonusApplyVo> getAllStuBonusApplyVo(int userId);

    /**
     * 教师通过学生学号查询该学生的加分申请表
     *
     * @param yearId    学年id
     * @param stuNumber 学生学号
     * @param approval  审核状态
     * @return 加分申请表信息
     */
    List<BonusApplyVo> getBonusApplyByStuNumberYearId(Integer yearId, String stuNumber, Integer approval);
}
