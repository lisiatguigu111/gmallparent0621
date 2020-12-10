package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author mqx
 * @date 2020-12-4 14:17:11
 */
@RestController
@RequestMapping("api/item") // 定义映射路径
public class ItemApiController {

    @Autowired
    private ItemService itemService;

    @GetMapping("{skuId}")
    public Result getItemBySkuId(@PathVariable Long skuId){
        //  调用服务层方法
        Map<String, Object> map = itemService.getBySkuId(skuId);
        //  返回给web-all 使用
        return Result.ok(map);
    }
}
