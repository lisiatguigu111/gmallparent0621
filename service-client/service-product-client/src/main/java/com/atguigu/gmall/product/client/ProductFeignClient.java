package com.atguigu.gmall.product.client;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.client.impl.ProductDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(name = "service-product",fallback = ProductDegradeFeignClient.class)
public interface ProductFeignClient {
    /**
     *
     * @param skuId
     * @return
     * api/product/inner/getSkuInfo/{skuId}
     */
    @GetMapping("api/product/inner/getSkuInfo/{skuId}")
     SkuInfo getSkuInfo(@PathVariable Long skuId);

    /**
     * 通过三级分类id查询分类信息
     * @param category3Id
     * @return
     */
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable Long category3Id);

    /**
     * 获取sku最新价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable Long skuId);

    /**
     * 根据spuId，skuId 查询销售属性集合
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId, @PathVariable("spuId") Long spuId);

    /**
     * 根据spuId 查询map 集合属性
     * @param spuId
     * @return
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    Map getSkuValueIdsMap(@PathVariable Long spuId);


    /**
     * 获取全部分类信息
     * @return//获取分类数据
     *     @GetMapping("getBaseCategoryList")
     *     public Result getBaseCategoryList()
     */
    @GetMapping("/api/product/getBaseCategoryList")
     Result getBaseCategoryList();

    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId
     * @return
     */
    @GetMapping("api/product/inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable("tmId")Long tmId);

    /**
     * 通过skuId 查询平台属性+平台属性值
     * @param skuId
     * @return
     */
    @GetMapping("api/product/inner/getAttrList/{skuId}")
    public List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId);
}
