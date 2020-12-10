package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean
   public ThreadPoolExecutor threadPoolExecutor(){
        //创建线程池
        return new ThreadPoolExecutor(
                3,
                5,
                3L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100)
        );
    }
}
