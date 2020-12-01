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
@CrossOrigin
public class BaseManageController {
    @Autowired
    private ManageService manageService;
    //查询所有一级分类数据
//    http://api.gmall.com/admin/product/getCategory1
    @GetMapping("getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> baseCategoryList = manageService.findAll();
        //将数据返回
        return Result.ok(baseCategoryList);
    }

    //http://api.gmall.com/admin/product/getCategory2/{category1Id}
    /**
     * 根据一级分类id获取二级分类
     */
    @GetMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") long category1Id){
        List<BaseCategory2> category2List = manageService.getCategory2(category1Id);
        return Result.ok(category2List);
    }
    //根据二级分类id查询三级分类
    /**
     * http://api.gmall.com/admin/product/getCategory3/{category2Id}
     */
    @GetMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") long category2Id){
        List<BaseCategory3> category3List = manageService.getCategory3(category2Id);
        return Result.ok(category3List);
    }

    //根据分类id查询所有数据(平台属性)
    //http://api.gmall.com/admin/product/attrInfoList/{category1Id}/{category2Id}/{category3Id}
    @GetMapping ("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(
                               @PathVariable("category1Id") long category1Id,
                               @PathVariable("category2Id") long category2Id,
                               @PathVariable("category3Id") long category3Id ){

        List<BaseAttrInfo> attrInfoList = manageService.getAttrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(attrInfoList);
    }

    //http://api.gmall.com/admin/product/saveAttrInfo
    //添加数据(保存数据)
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){

        //调用服务层方法
        manageService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 根据平台属性ID获取平台属性对象数据
     * http://api.gmall.com/admin/product/getAttrValueList/{attrId}
     */
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") long attrId){
        //查询平台属性值的集合
        //select * from base_attr_value where attr_id = attrId
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo(attrId);
        return  Result.ok(baseAttrInfo.getAttrValueList());
    }

}
