package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.service.GoodsRepository;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
/**
 * @author mqx
 * @date 2020-12-9 11:37:29
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ProductFeignClient productFeignClient;

    //  注入谁? 操作es 的对象; CURD
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void upperGoods(Long skuId) {
        //  声明变量
        Goods goods = new Goods();
        //  此处goods 还是null，赋值
        //  获取skuInfo;
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            goods.setId(skuId);
            goods.setTitle(skuInfo.getSkuName());
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setPrice(skuInfo.getPrice().doubleValue());
            goods.setCreateTime(new Date());
            return skuInfo;
        });

        CompletableFuture<Void> completableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            //  获取分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            goods.setCategory1Id(categoryView.getCategory1Id());
            goods.setCategory1Name(categoryView.getCategory1Name());
            goods.setCategory2Id(categoryView.getCategory2Id());
            goods.setCategory2Name(categoryView.getCategory2Name());
            goods.setCategory3Id(categoryView.getCategory3Id());
            goods.setCategory3Name(categoryView.getCategory3Name());

        }));


        CompletableFuture<Void> trademarkCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            //  获取品牌数据
            BaseTrademark trademark = productFeignClient.getTrademark(skuInfo.getTmId());
            goods.setTmId(trademark.getId());
            goods.setTmName(trademark.getTmName());
            goods.setTmLogoUrl(trademark.getLogoUrl());
        }));

        CompletableFuture<Void> attrListCompletableFuture = CompletableFuture.runAsync(() -> {
            List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
            //  整一个List<SearchAttr>
            List<SearchAttr> searchAttrList = new ArrayList<>();
            attrList.stream().forEach((baseAttrInfo -> {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(baseAttrInfo.getId());
                searchAttr.setAttrName(baseAttrInfo.getAttrName());
                searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
                searchAttrList.add(searchAttr);

            }));

        //            List<Object> collect = attrList.stream().map((baseAttrInfo -> {
        //                SearchAttr searchAttr = new SearchAttr();
        //                searchAttr.setAttrId(baseAttrInfo.getId());
        //                searchAttr.setAttrName(baseAttrInfo.getAttrName());
        //                searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
        //                searchAttrList.add(searchAttr);
        //                return searchAttr;
        //            })).collect(Collectors.toList());
        //            goods.setAttrs(collect);

            goods.setAttrs(searchAttrList);
        });

        CompletableFuture.allOf(
                skuInfoCompletableFuture,
                completableFuture,
                trademarkCompletableFuture,
                attrListCompletableFuture
        ).join();
        //  上架
        this.goodsRepository.save(goods);
    }

    //商品下架
    @Override
    public void lowerGoods(Long skuId) {
        //  删除数据
        this.goodsRepository.deleteById(skuId);
    }


    //实现热度排名
    @Override
    public void incrHotScore(Long skuId) {
        //被访问的次数存储到redis
       //记录被访问的次数+1
        //先确定key
        String hotKey = "hotScore";

        //再确定数据类型  zset
        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotKey, "skuId:" + skuId, 1);
        if(hotScore %10 == 0){
            //更新
            Optional<Goods> optional = this.goodsRepository.findById(skuId);
            Goods goods = optional.get();
            goods.setHotScore(hotScore.longValue());
            //保存
            this.goodsRepository.save(goods);
        }
    }
}
