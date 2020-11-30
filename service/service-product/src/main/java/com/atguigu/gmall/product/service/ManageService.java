package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.util.List;

public interface ManageService  {
    //查询所有一级分类
    List<BaseCategory1> findAll();

    //根据一级分类id查询所有二级分类数据
    List<BaseCategory2> getCategory2(Long category1Id);

    //根据二级分类id查询所有三级分类数据
    List<BaseCategory3> getCategory3(Long category2Id);

    //根据分类id查询平台属性
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, long category3Id);

    //保存平台属性+平台属性值
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    //根据平台属性id 获取平台属性值集合
    List<BaseAttrValue> getAttrValueList(long attrId);
    //根据平台属性id 获取平台属性对象
    BaseAttrInfo getBaseAttrInfo(long attrId);
}
