package cn.ccsu.cecs.oos.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.io.File;

@Slf4j
@Configuration
public class Config {

    // OS 系统分隔符
    public static final String FILES_SEPARATOR = System.getProperty("file.separator");
    // 项目名称
    public static final String PROJECT_NAME = "ccsu-cecs";
    // oos存储根路径
    public static final String SAVE_OOS_ROOT_PATH = StringUtils.join(System.getProperty("user.home"),
            FILES_SEPARATOR, PROJECT_NAME, FILES_SEPARATOR, "oos", FILES_SEPARATOR);
    // 文件oos图片根路径
    public final static String FILE_ROOT_PATH = Config.SAVE_OOS_ROOT_PATH + "images" + Config.FILES_SEPARATOR;

    /**
     * 文件分隔符
     *
     * @return 文件分隔符
     */
    @Primary
    @Scope(value = "singleton")
    @Bean(name = "fileSeparator")
    public String fileSeparator() {
        log.info("OS file separator : {}", FILES_SEPARATOR);
        return FILES_SEPARATOR;
    }

    /**
     * 项目名称
     *
     * @return 项目名称
     */
    @Primary
    @Scope(value = "singleton")
    @Bean(name = "projectName")
    public String projectName() {
        log.info("project name : {}", PROJECT_NAME);
        return PROJECT_NAME;
    }

    /**
     * oos存储根路径
     *
     * @return oos存储根路径
     */
    @Primary
    @Scope(value = "singleton")
    @Bean(name = "saveOosRootPath")
    public String saveOosRootPath() {
        log.info("save image root path : {}", SAVE_OOS_ROOT_PATH);
        createSaveImagePath(SAVE_OOS_ROOT_PATH);
        return SAVE_OOS_ROOT_PATH;
    }

    /**
     * 图片存储根路径
     *
     * @return 图片存储根路径
     */
//    @Primary
    @Scope(value = "singleton")
    @Bean(name = "saveImagePath")
    public String saveImagePath() {
        String path = StringUtils.join(SAVE_OOS_ROOT_PATH, "images", FILES_SEPARATOR);
        log.info("saveImagePath : {}", path);
        createSaveImagePath(path);
        return path;
    }

    /**
     * 图片 url
     *
     * @return 图片url
     */
    @Primary
    @Scope(value = "singleton")
    @Bean(name = "imageUrl")
    public String imageUrl() {
        return "/oos/images/";
    }

    /**
     * 创建图片保存路径
     *
     * @param path 路径
     */
    public static void createSaveImagePath(String path) {
        File dir = new File(path);
        String message = "dir exists.";
        // 如果不存在，则创建
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                message = "create dir success.";
            } else {
                message = "create dir fail.";
            }
        }
        log.info("{} {}", path, message);
    }


    /**
     * <p>
     * 开启分页
     * </p>
     * 参考： https://www.kuangstudy.com/bbs/1363370653402537985
     *
     * @return MybatisPlusInterceptor 插件
     */
    @Primary
    @Scope(value = "singleton")
    @Bean(name = "mybatisPlusInterceptor")
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 声明当前使用的是MySQL数据库
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
