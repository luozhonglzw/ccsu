package cn.ccsu.cecs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程配置类
 */
@Configuration
public class ThreadConfig {

    @Primary
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties poolConfigProperties) {
        return new ThreadPoolExecutor(
                poolConfigProperties.getCoreSize(),
                poolConfigProperties.getMaxSize(),
                poolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2000),
                new DefaultManagedAwareThreadFactory(),   // 默认线程工厂
                new ThreadPoolExecutor.DiscardPolicy()  // 拒绝策略
        );
    }

//    @Bean
//    public RefreshThreadPoolExecutor refreshThreadPoolExecutor(ThreadPoolConfigProperties poolConfigProperties){
//        return new RefreshThreadPoolExecutor(
//                DEFAULT_THREAD_COUNT,
//                20,
//                poolConfigProperties.getKeepAliveTime(),
//                TimeUnit.SECONDS,
//                new LinkedBlockingQueue<>(2000),
//                new DefaultManagedAwareThreadFactory(),   // 默认线程工厂
//                new ThreadPoolExecutor.DiscardPolicy()  // 拒绝策略
//        );
//    }
//
//    public static class RefreshThreadPoolExecutor extends ThreadPoolExecutor{
//
//        public RefreshThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
//            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
//        }
//    }
}
