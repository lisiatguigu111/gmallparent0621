package com.atguigu.gmall.list.service;

/**
 * @author mqx
 * @date 2020-12-9 11:36:34
 */
public interface SearchService {

    //  商品上架
    void upperGoods(Long skuId);
    //  商品下架
    void lowerGoods(Long skuId);
    //热度排名
     void incrHotScore(Long skuId);

}
