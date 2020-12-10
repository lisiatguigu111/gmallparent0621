package com.atguigu.gmall.common.cache;

import javax.xml.bind.Element;
import java.lang.annotation.*;

@Target(ElementType.METHOD)//表示注解的使用范围
@Retention(RetentionPolicy.RUNTIME)//注解的生命周期
@Documented
public @interface GmallCache {
    //定义前缀
    String prefix()default "cache";
}
