package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping("admin/product")
@Api(tags = "spuinfo 数据接口")
public class SpuManageController {

    @Autowired
    private ManageService manageService;


//    http://api.gmall.com/admin/product/ {page}/{limit}?category3Id=61
    @GetMapping("{page}/{limit}")
    public Result getSpuList(@PathVariable long page,
                             @PathVariable long limit,
//                              获取三级分类id参数传递的v方式
//                                 HttpServletRequest request,
//                             Spring category3Id,
                               SpuInfo spuInfo){

//        String category3Id = request.getParameter("category3Id");
        //处理分页查询
        Page<SpuInfo> spuInfoPage = new Page<>(page,limit);
        // 调用服务层方法
        IPage spuInfoPageList = manageService.getSpuInfoPage(spuInfoPage, spuInfo);
        //返回数据
        return Result.ok(spuInfoPageList);

    }

    //获取销售属性
    //http://api.gmall.com/admin/product/baseSaleAttrList
    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        //返回销售属性集合
       List<BaseSaleAttr> baseSaleAttrList =  manageService.getBaseSaleAttrList();
        return Result.ok(baseSaleAttrList);
    }

    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
      manageService.saveSpuInfo(spuInfo);
       return Result.ok();
    }


    //回显所有spuImageList
    //http://api.gmall.com/admin/product/spuSaleAttrList/16

    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId){
        //  调用服务层方法
        List<SpuImage> spuImageList = manageService.getSpuImageList(spuId);
        return Result.ok(spuImageList);
    }
}
