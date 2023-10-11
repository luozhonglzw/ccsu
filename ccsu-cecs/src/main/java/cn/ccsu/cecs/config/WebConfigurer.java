package cn.ccsu.cecs.config;

import cn.ccsu.cecs.common.interceptor.BaseInterceptor;
import cn.ccsu.cecs.common.interceptor.TeacherInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;

/**
 * web 配置类
 */
@Slf4j
@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    // 学生业务拦截器path
    private static final String[] STUDENT_INTERCEPTOR = {
            // 学生相关业务统一使用/student/**(StuStudentController、StuProfessionController、StuGradeController、
            // StuCollegeController、StuClassController)
            "/student/**",
            // 加分项业务统一使用(BonusApplyController、BonusApplyImageController、BonusBonusController
            // BonusCategoryController、BonusYearController)
            "/bonus/**"
    };

    // oos业务拦截器path
    private static final String[] OOS_INTERCEPTOR = {
            // oos相关业务统一使用/oos/**(OosImagesController)
            "/oos/**",
    };

    // 后台业务拦截器path
    private static final String[] ADMIN_INTERCEPTOR = {
            // 后台相关业务统一使用/stag-backend/**
            "/stag-backend/**",
    };

    // 老师端业务拦截器path
    private static final String[] TEACHER_INTERCEPTOR = {
            // 老师端相关业务统一使用/teacher/**
            "/teacher/**",
    };

    @Resource(name = "saveOosRootPath")
    private String saveOosRootPath;

    /**
     * 添加资源处理器
     * 用于映射${USER_PATH}/${PROJECT_NAME}/oos中的文件资源
     *
     * @param registry 资源添加
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/oos/**").addResourceLocations("file:" + saveOosRootPath);
    }

    @Bean
    public TeacherInterceptor teacherInterceptor(){
        return new TeacherInterceptor();
    }

    /**
     * 登录拦截器
     *
     * @param registry 注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // admin 端拦截器
        registry.addInterceptor(new BaseInterceptor("admin")).addPathPatterns(ADMIN_INTERCEPTOR);
        // student 端拦截器
        registry.addInterceptor(new BaseInterceptor("student")).addPathPatterns(STUDENT_INTERCEPTOR)
                .excludePathPatterns("/student/login");
        // teacher 端拦截器
        registry.addInterceptor(teacherInterceptor()).addPathPatterns(TEACHER_INTERCEPTOR)
                .excludePathPatterns("/teacher/login");
    }

    /**
     * 配置全局 cors
     *
     * <p>1.允许访问所有的api</p>
     * <p>2.允许任何域名使用</p>
     * <p>3.允许任何头使用</p>
     * <p>4.允许任何方法使用</p>
     *
     * @param registry cors注册
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("*") // api 映射
                .allowedOrigins("*") // 允许任何域名使用
                .allowedHeaders("*") // 允许任何头
                .allowedMethods("*")// 允许任何方法（post、get等）
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("test1").setViewName("test");
//        registry.addViewController("test2").setViewName("test2");
//        registry.addViewController("test_upload").setViewName("test_upload");
    }
}
