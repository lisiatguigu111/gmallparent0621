package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public synchronized void testLock() {
        //获取缓存数据
        //opsForValue：String数据类型  放入数据的方法
        String values = redisTemplate.opsForValue().get("num");
        if(StringUtils.isEmpty(values)){
            return;
        }
        //true ++num 再放入缓存
        int num = Integer.parseInt(values);

        redisTemplate.opsForValue().set("num",String.valueOf(++num));

    }
}
