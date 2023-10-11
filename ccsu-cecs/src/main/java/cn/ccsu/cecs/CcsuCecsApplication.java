package cn.ccsu.cecs;

import cn.ccsu.cecs.oos.service.impl.OosImagesServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//@EnableScheduling  // 开启定时任务
@EnableAsync
@EnableTransactionManagement // 开启事务
@SpringBootApplication
public class CcsuCecsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcsuCecsApplication.class, args);
    }

}
