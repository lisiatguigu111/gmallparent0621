package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "品牌数据接口")
@RestController//@ResponseBody + @Controller
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {


    @Autowired
    private BaseTrademarkService baseTrademarkService;



    //http://api.gmall.com/admin/product/baseTrademark/{page}/{limit}
    @GetMapping("{page}/{limit}")
    public Result getSpuList(@PathVariable long page,
                             @PathVariable long limit){
        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, limit);
        IPage baseTradeMarkList = baseTrademarkService.getBaseTradeMarkList(baseTrademarkPage);
        return Result.ok(baseTradeMarkList);

    }

    //添加品牌
    //http://api.gmall.com/admin/product/baseTrademark/save
    //使用@RequestBody转换为java对象
    @PostMapping("save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
     //独自写一个BaseTrademarkService
        baseTrademarkService.save(baseTrademark);
       return Result.ok();
    }

    //http://api.gmall.com/admin/product/baseTrademark/update
    //修改品牌
    @PutMapping("update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    //http://api.gmall.com/admin/product/baseTrademark/remove/{id}
    //删除品牌
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }
    //根据id获取品牌数据
    //http://api.gmall.com/admin/product/baseTrademark/get/{id}
    @GetMapping("get/{id}")
    public Result getById(@PathVariable long id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return  Result.ok(baseTrademark);

    }

    //http://api.gmall.com/admin/product/baseTrademark/getTrademarkList
    //获取品牌属性
    @GetMapping("getTrademarkList")
    public Result getTrademarkList(){
            //select * from base_trademark;
        return  Result.ok(baseTrademarkService.list(null));
    }

}
