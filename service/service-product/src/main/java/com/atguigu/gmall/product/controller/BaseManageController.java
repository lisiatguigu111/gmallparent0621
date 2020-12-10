package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "后台数据接口")
@RestController//@ResponseBody + @Controller
@RequestMapping("admin/product")
//@CrossOrigin
public class BaseManageController {

    @Autowired
    private ManageService manageService;

    //  控制器。。
    //  查询所有的一级分类数据：
    //  http://api.gmall.com/admin/product/getCategory1
    @GetMapping("getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> baseCategory1List = manageService.findAll();
        //  将数据返回
        return Result.ok(baseCategory1List);
    }

    //根据一级分类id查询二级分类数据
    //  http://api.gmall.com/admin/product/getCategory2/{category1Id}
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable Long category1Id){
        List<BaseCategory2> category2List = manageService.getCategory2(category1Id);
        //  将数据返回
        return Result.ok(category2List);
    }

    //根据二级分类数据查询三级分类数据
    //  http://api.gmall.com/admin/product/getCategory3/{category2Id}
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable Long category2Id){
        List<BaseCategory3> category3List = manageService.getCategory3(category2Id);
        //  将数据返回
        return Result.ok(category3List);
    }

    //查询平台属性
    //  http://api.gmall.com/admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}
    @GetMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable Long category1Id,
                               @PathVariable Long category2Id,
                               @PathVariable Long category3Id){
        List<BaseAttrInfo> attrInfoList = manageService.getAttrInfoList(category1Id, category2Id, category3Id);
        //  将数据返回
        return Result.ok(attrInfoList);
    }

    //保存平台属性

    //  http://api.gmall.com/admin/product/saveAttrInfo
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        //  调用服务层方法
        manageService.saveAttrInfo(baseAttrInfo);
        //  返回
        return Result.ok();
    }

    //  http://api.gmall.com/admin/product/getAttrValueList/{attrId}
    //  查询 平台属性值的集合
    //  select * from base_attr_value where attr_id = attrId;
    //  List<BaseAttrValue> baseAttrValueList =  manageService.getAttrValueList(attrId);
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        //  可以先通过attrId ，获取平台属性baseAttrInfo！    如果属性存在，则获取属性中对应的属性值 getAttrValueList();
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo(attrId);

        return Result.ok(baseAttrInfo.getAttrValueList());
    }



}
