package cn.ccsu.cecs.admin.service;


import cn.ccsu.cecs.admin.entity.AdminUser;
import cn.ccsu.cecs.admin.vo.AdminLoginVo;
import cn.ccsu.cecs.common.entity.BaseInfo;
import cn.ccsu.cecs.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.service.IService;

import java.net.UnknownHostException;
import java.util.Map;

/**
 * 学院表
 *
 * @author ccsu-cecs
 */
public interface AdminUserService extends IService<AdminUser> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 处理管理员登录
     *
     * @param adminLoginVo 登录信息
     */
    void login(AdminLoginVo adminLoginVo) throws UnknownHostException;

    /**
     * 修改管理员密码
     *
     * @param adminLoginVo 管理员信息
     */
    void modifyPassword(AdminLoginVo adminLoginVo);

    /**
     * 获取所有学院、年级、专业、班级信息
     *
     * @return 所有基本信息
     */
    BaseInfo getBaseInfo();

    /**
     * 重置学生密码
     *
     * @param stuNumber   学号
     * @param teacherName 老师名称
     */
    void resetPassword(String stuNumber, String teacherName);
}

