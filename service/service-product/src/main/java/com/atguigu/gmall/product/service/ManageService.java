package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    /**
     *
     * @param spuInfoPage
     * @param spuInfo
     * @return
     */
    IPage getSpuInfoPage(Page<SpuInfo> spuInfoPage , SpuInfo spuInfo);


    /**
     *
     * @return
     * 获取销售属性集合
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    void saveSpuInfo(SpuInfo spuInfo);


    /**
     * 回显所有spuImageList
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 根据spuid 获取销售属性+销售属性值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(long spuId);

    /**
     *  //保存sku数据
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 查询所有skuInfo数据
     * @param skuInfoPage
     * @return
     */
    IPage<SkuInfo> getSkuInfoList(Page<SkuInfo> skuInfoPage);

    /**
     * 商品上架
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     * 商品下架
     * @param skuId
     */
    void cancelSale(Long skuId);

    /**
     * 根据skuId获取商品的基本信息
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfoById(Long skuId);

    /**
     * 根据三级分类id查询分类名称
     * @param category3Id
     * @return
     */
    BaseCategoryView  getCategoryViewByCategory3Id(Long category3Id);

    /**
     * 获取Sku价格
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);

    /**
     * 根据spuId查询销售属性和销售属性值
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId,Long spuId);

    //根据spuId获取spu对应的销售属性值组成的skuId集合
    Map getSkuValueIdsMap(Long spuId);

    //查询首页分类数据
    List<JSONObject>  getBaseCategoryList();

    /**
     * 通过品牌Id 来查询数据
     * @param tmId
     * @return
     */
    BaseTrademark getTrademarkByTmId(Long tmId);

    /**
     * 通过skuId 集合来查询数据
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getAttrList(Long skuId);
}
