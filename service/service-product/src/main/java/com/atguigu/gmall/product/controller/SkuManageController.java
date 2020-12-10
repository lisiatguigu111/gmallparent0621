package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/product")
@Api(tags = "SKU数据接口")
public class SkuManageController {

    @Autowired
    private ManageService manageService;

    //http://api.gmall.com/admin/product/spuSaleAttrList/{spuId}
    //根据spuid 获取销售属性+销售属性值
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable long spuId){
        //调用服务层方法
       List<SpuSaleAttr> spuSaleAttrList =  manageService.getSpuSaleAttrList(spuId);
       return Result.ok(spuSaleAttrList);
    }

    //保存
    //http://api.gmall.com/admin/product/saveSkuInfo
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        //调用服务层方法
        manageService.saveSkuInfo(skuInfo);

        //返回数据
        return  Result.ok();
    }
    //http://api.gmall.com/admin/product/list/{page}/{limit}
    //获取sku分页列表
    @GetMapping("list/{page}/{limit}")
    public Result getSkuInfoList(@PathVariable long page,
                                 @PathVariable long limit){
        //声明一个page
        Page<SkuInfo> skuInfoPage = new Page<>(page,limit);

        //调用服务层

        //查询所有数据
       IPage<SkuInfo> skuInfoIPage =  manageService.getSkuInfoList(skuInfoPage);
       //返回数据
        return Result.ok(skuInfoIPage);
    }

    //商品上架
    //  http://api.gmall.com/admin/product/onSale/{skuId}
    @GetMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId){
            //调用服务层
        manageService.onSale(skuId);
        return Result.ok();
    }

    //商品下架
    //  http://api.gmall.com/admin/product/cancelSale/{skuId}
    @GetMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId ){
        manageService.cancelSale(skuId);
        return Result.ok();
    }



}
