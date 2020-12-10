package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.constant.RedisConst;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 切注解完成分布式锁的业务逻辑
 */
@Component
@Aspect
public class GmallCacheAspect {
    @Autowired
   private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    //在环绕通知未进入方法体内部的时候 无法确认器返回值，一旦切入到方法体内部 就知道具体的返回值了。
    @SneakyThrows
    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint){
        Object object = new Object();
        try {
            //分布式锁的业务逻辑
            //需要缓存的key，这个缓存的key是有注解的前缀+方法体的参数组成
            //先找注解，注解在方法上，判断方法是是否有方法
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            //获取到方法上的注解
            GmallCache gmallCache = methodSignature.getMethod().getAnnotation(GmallCache.class);
            //获取到前缀
            String prefix = gmallCache.prefix();
            //获取方法的参数
            Object[] args = joinPoint.getArgs();
            //组成缓存的key
            String key  = prefix + Arrays.asList(args).toString();

            //获取缓存中的数据redisTemplate.opsForValue().get(key);
            //如果获取缓存中的数据，需要将数据转换为方法的返回值类型
            //methodSignature.getReturnType() 获取方法的返回值类型
            object =cacheHit(key,methodSignature);

            //判断
            if(object == null){
                //缓存没有数据· 上锁
                //锁的key=注解的前缀+方法参数+lock
                RLock lock = redissonClient.getLock(key+"lock");
                boolean flag = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);
                //判断上锁是否成功
                if(flag){
                    try {
                        //表示上锁成功，查询数据库
                        //执行方法体 joinPoint.proceed如果在方法上能够找到注解，表示要执行次方法体
                        object =  joinPoint.proceed(joinPoint.getArgs());
                        //防止缓存穿透
                        if(object == null){
                            Object object1 = new Object();
                            redisTemplate.opsForValue().set(key, JSON.toJSONString(object),RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                            return object1;
                        }
                        //如果不为空
                        redisTemplate.opsForValue().set(key,JSON.toJSONString(object),RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                        return object;
                    } finally {
                        //解锁
                        lock.unlock();
                    }

                }else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //自旋
                    return cacheAroundAdvice(joinPoint);
                }
            }else {
                //缓存有数据
                return object;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

    private Object cacheHit(String key, MethodSignature methodSignature) {
        //获取缓存中的数据
        String  strValue = (String) redisTemplate.opsForValue().get(key);
        //判断获取的数据不为空，返回数据+数据类型
        if(!StringUtils.isEmpty(strValue)){
            Class returnType = methodSignature.getReturnType();
            //返回数据
            return JSON.parseObject(strValue,returnType);
        }
        return null;
    }
}
