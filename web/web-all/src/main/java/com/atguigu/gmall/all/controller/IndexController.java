package com.atguigu.gmall.all.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class IndexController {
   @Autowired
   private ProductFeignClient productFeignClient;
    //控制器
    @RequestMapping("index.html")
    public String index(Model model){
        //后台储存一个list
        Result result = productFeignClient.getBaseCategoryList();
        model.addAttribute("list",result.getData());
        //返回页面
        return "index/index";

    }
    @RequestMapping("/")
    public String index1(Model model){
        Result result = productFeignClient.getBaseCategoryList();
        model.addAttribute("list",result.getData());
        return "index/index";
    }
}
