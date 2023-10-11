package cn.ccsu.cecs.admin.service.impl;

import cn.ccsu.cecs.admin.dao.AdminUserDao;
import cn.ccsu.cecs.admin.entity.AdminUser;
import cn.ccsu.cecs.admin.service.AdminUserService;
import cn.ccsu.cecs.admin.vo.AdminLoginVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.entity.BaseInfo;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.service.*;
import cn.ccsu.cecs.student.utils.StuUtils;
import cn.ccsu.cecs.student.vo.ClassVo;
import cn.ccsu.cecs.student.vo.CollegeVo;
import cn.ccsu.cecs.student.vo.GradeVo;
import cn.ccsu.cecs.student.vo.ProfessionVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;


@Service("adminUserService")
public class AdminUserServiceImpl extends ServiceImpl<AdminUserDao, AdminUser> implements AdminUserService {

    @Autowired
    IStuStudentService stuStudentService;

    @Autowired
    DefaultCache defaultCache;

    @Autowired
    TeacherInterceptor teacherInterceptor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AdminUser> page = this.page(
                new Query<AdminUser>().getPage(params),
                new QueryWrapper<AdminUser>()
        );

        return new PageUtils(page);
    }

    @Override
    public void login(AdminLoginVo adminLoginVo) throws UnknownHostException {
        String password = adminLoginVo.getPassword();
        // md5加密
        String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        AdminUser adminUser = this.getOne(new QueryWrapper<AdminUser>()
                .eq("username", adminLoginVo.getUsername())
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        // 如果账号不存在或者密码比对失败
        if (adminUser == null || !adminUser.getPassword().equalsIgnoreCase(encryptPassword)) {
            throw new RuntimeException("账号密码错误");
        } else {
            adminLoginVo.setId(adminUser.getId());
            adminLoginVo.setName(adminUser.getName());
        }
    }

    @Override
    public void modifyPassword(AdminLoginVo adminLoginVo) {
        String password = adminLoginVo.getPassword();
        String encryptPassword = DigestUtils.md5DigestAsHex(password.getBytes());

        boolean update = this.update(new UpdateWrapper<AdminUser>()
                .set("password", encryptPassword).eq("id", adminLoginVo.getId()));
        if (!update) {
            throw new RuntimeException("修改密码异常");
        } else {
            this.update(new UpdateWrapper<AdminUser>()
                    .set("updated_at", new Date())
                    .set("updated_by", adminLoginVo.getName())
                    .eq("id", adminLoginVo.getId()));
        }
    }

    @Override
    public BaseInfo getBaseInfo() {

        Map<Integer, String> idAndCollegeNameMap = defaultCache.getIdAndCollegeNameMap();
        List<CollegeVo> collegeVos = new ArrayList<>();
        idAndCollegeNameMap.forEach((id, collegeName) -> {
            collegeVos.add(new CollegeVo(id, collegeName));
        });

        Map<Integer, String> idAndGradeNameMap = defaultCache.getIdAndGradeNameMap();
        List<GradeVo> gradeVos = new ArrayList<>();
        idAndGradeNameMap.forEach((id, gradeName) -> {
            gradeVos.add(new GradeVo(id, gradeName));
        });

        Map<Integer, String> idAndProfessionNameMap = defaultCache.getIdAndProfessionNameMap();
        List<ProfessionVo> professionVos = new ArrayList<>();
        idAndProfessionNameMap.forEach((id, professionName) -> {
            professionVos.add(new ProfessionVo(id, professionName));
        });

        Map<Integer, String> idAndClassNameMap = defaultCache.getIdAndClassNameMap();
        List<ClassVo> classVos = new ArrayList<>();
        idAndClassNameMap.forEach((id, className) -> {
            classVos.add(new ClassVo(id, className));
        });

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setCollegeVos(collegeVos);
        baseInfo.setGradeVos(gradeVos);
        baseInfo.setProfessionVos(professionVos);
        baseInfo.setClassVos(classVos);
        return baseInfo;
    }

    /**
     * 重置学生密码
     *
     * @param stuNumber   学号
     * @param teacherName 老师名称
     */
    @Override
    public void resetPassword(String stuNumber, String teacherName) {
        StuUtils.checkStuNumber(stuNumber);

        StuStudent stuStudent = stuStudentService.getOne(new QueryWrapper<StuStudent>()
                .eq("stu_number", stuNumber)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));

        if (stuStudent == null) {
            throw new RuntimeException("学生学号信息不存在");
        }

        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex(ProjectConstant.STUDENT_DEFAULT_PASSWORD.getBytes());
        // 更新
        stuStudentService.update(new UpdateWrapper<StuStudent>().set("password", encryptPassword)
                .set("updated_by", teacherName)
                .set("updated_at", new Date())
                .eq("stu_number", stuNumber));
    }
}