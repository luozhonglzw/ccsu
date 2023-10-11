package cn.ccsu.cecs;

import cn.ccsu.cecs.oos.controller.teacher.TeacherOosImagesController;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import cn.ccsu.cecs.oos.service.impl.OosImagesServiceImpl;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

@Slf4j
@SpringBootTest(classes = CcsuCecsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CcsuCecsApplicationTests {

    /**
     * 单元测试
     */
    @Test
    void contextLoads() {
        log.info("hello world");
    }

    /**
     * 使用mybatis-plus生成学生模块代码
     */
    @Test
    void test_FastAutoGeneratorStudentModule() {
        // todo : 配置db连接信息
        FastAutoGenerator.create(
                        "jdbc:mysql://127.0.0.1:3306/ccsu-cecs?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC",
                        "root", "root")
                .globalConfig(builder -> {
                    builder.author("ccsu-cecs") // 设置作者
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:/Project/Java/ccsu-cecs/src/main/java"); // todo: 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("cn.ccsu.cecs") // 设置父包名
                            .moduleName("student") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,
                                    "D:/Project/Java/ccsu-cecs/src/main/resources/mapper/student")); // todo: 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
//                     设置需要生成的表名
                    builder.addInclude("stu_college")
                            .addInclude("stu_grade")
                            .addInclude("stu_profession")
                            .addInclude("stu_class")
                            .addInclude("stu_student")
                            .addInclude("oos_images")
                            .addInclude("bonus_year")
                            .addInclude("bonus_category")
                            .addInclude("bonus_bonus")
                            .addInclude("bonus_apply_image")
                            .addInclude("bonus_comprehensive_score")
                            .addInclude("bonus_apply");
                    // 设置过滤表前缀
                    // .addTablePrefix("x_", "c_");

                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

    /**
     * 使用mybatis-plus生成加分项模块代码
     */
    @Test
    void test_FastAutoGeneratorBonusModule() {
        // todo : 配置db连接信息
        FastAutoGenerator.create(
                        "jdbc:mysql://127.0.0.1:3306/ccsu-cecs?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC",
                        "root", "root")
                .globalConfig(builder -> {
                    builder.author("ccsu-cecs") // 设置作者
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:/Project/Java/ccsu-cecs/src/main/java"); // todo: 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("cn.ccsu.cecs") // 设置父包名
                            .moduleName("bonus") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,
                                    "D:/Project/Java/ccsu-cecs/src/main/resources/mapper/bonus")); // todo: 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    // 设置需要生成的表名
                    builder.addInclude("bonus_year")
                            .addInclude("bonus_category")
                            .addInclude("bonus_bonus")
                            .addInclude("bonus_apply_image")
                            .addInclude("bonus_apply");
                    // 设置过滤表前缀
                    // .addTablePrefix("x_", "c_");

                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

    /**
     * 使用mybatis-plus生成oos模块代码
     */
    @Test
    void test_FastAutoGeneratorOosModule() {
        // todo : 配置db连接信息
        FastAutoGenerator.create(
                        "jdbc:mysql://127.0.0.1:3306/ccsu-cecs?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC",
                        "root", "root")
                .globalConfig(builder -> {
                    builder.author("ccsu-cecs") // 设置作者
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:/Project/Java/ccsu-cecs/src/main/java"); // todo: 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("cn.ccsu.cecs") // 设置父包名
                            .moduleName("oos") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml,
                                    "D:/Project/Java/ccsu-cecs/src/main/resources/mapper/oos")); // todo: 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    // 设置需要生成的表名
                    builder.addInclude("oos_images");
                    // 设置过滤表前缀
                    // .addTablePrefix("x_", "c_");

                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

    @Autowired
    private TeacherOosImagesController oosImagesController;

    @Test
    public  void test(){
        val byId = oosImagesController.info(23);
        System.out.println(byId);
    }
}
