package cn.ccsu.cecs.yangjr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Component
public class EventTest implements ApplicationListener<YangEvent> {

    int count = 0;

    @Autowired
    ApplicationContext applicationContext;

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void onApplicationEvent(YangEvent event) {
        String name = Thread.currentThread().getName();
        System.out.println("线程名：" + name);
        Resource resource = event.getResource();

//        try { TimeUnit.SECONDS.sleep(4); } catch (Exception e) { e.printStackTrace(); }

        System.out.println("触发次数：" + count++ + "\t得到的资源名：" + resource.getName());
    }

    @GetMapping("/test")
    public void test(){
        int i = Thread.activeCount();
        String name = Thread.currentThread().getName();
        System.out.println("123线程名：" + name);

        System.out.println("未开启事件监听时，系统有： " + i + " 个线程");
        System.out.println("-----------------------------------");
        this.applicationContext.publishEvent(new YangEvent("123", new Resource("你好")));
        int i1 = Thread.activeCount();
        System.out.println("开启事件监听时，系统有： " + i1 + " 个线程");

    }
}
