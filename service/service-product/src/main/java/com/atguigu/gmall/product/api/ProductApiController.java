package com.atguigu.gmall.product.api;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductApiController {
    @Autowired
    private ManageService manageService;
    //根据skuId获取商品的基本信息+图片信息
    //inner不能通过浏览器直接访问  微服务内部调用数据接口
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){

            return manageService.getSkuInfoById(skuId);
    }

    //根据三级分类id查询分类数据

    /**
     * 根据三级分类id查询分类信息
     * @param category3Id
     * @return
     */
    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id){
       return manageService.getCategoryViewByCategory3Id(category3Id);
    }

    //根据skuId获取最新价格
    @GetMapping("inner/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable Long skuId){
       return manageService.getSkuPrice(skuId);
    }

    //根据skuId spuId回显销售属性+销售属性值+锁定
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId,
                                                          @PathVariable Long spuId){
        return manageService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    //返回切换数据组成的map  在service_item中转换json数据
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable Long spuId){
        return manageService.getSkuValueIdsMap(spuId);
    }
   //获取分类数据
    @GetMapping("getBaseCategoryList")
    public Result getBaseCategoryList(){
        List<JSONObject> list = manageService.getBaseCategoryList();
        return Result.ok(list);
    }
    /**发布数据接口
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping("inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable("tmId")Long tmId){
        return manageService.getTrademarkByTmId(tmId);
    }

    //发布数据接口
    //通过skuId 查询平台属性+平台属性值
    @GetMapping("inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId){
        return manageService.getAttrList(skuId);
    }



}
