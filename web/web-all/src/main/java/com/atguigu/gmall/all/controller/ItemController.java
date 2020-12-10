package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author mqx
 * @date 2020-12-5 11:27:43
 */
@Controller
public class ItemController {

    @Autowired
    private ItemFeignClient itemFeignClient;

    //  访问商品详情页面控制器
    //  http://item.gmall.com/{skuId}.html;
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable Long skuId, Model model){
        //  调用数据
        Result<Map> result = itemFeignClient.getItem(skuId);

        //  保存数据
         model.addAllAttributes(result.getData());

        //  返回视图名称
        return "item/index";
    }

}
