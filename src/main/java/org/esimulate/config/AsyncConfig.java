package org.esimulate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Value("${async.corePoolSize}")
    private int corePoolSize;

    @Value("${async.maxPoolSize}")
    private int maxPoolSize;

    @Value("${async.queueCapacity}")
    private int queueCapacity;

    @Value("${async.keepAliveSeconds}")
    private int keepAliveSeconds;

    @Value("${async.threadNamePrefix}")
    private String threadNamePrefix;

    @Bean("psoAsyncExecutor")
    public Executor psoAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 根据业务需求自行调整下面各项参数
        // 核心线程数：始终保持运行的线程数量
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数：能创建的最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 队列容量：当核心线程都在忙时，新的任务会被放到队列中排队
        executor.setQueueCapacity(queueCapacity);
        // 非核心线程空闲后保留时间（秒）
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 线程名前缀，方便定位
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}