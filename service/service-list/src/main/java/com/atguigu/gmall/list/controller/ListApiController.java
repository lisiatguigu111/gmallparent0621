package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mqx
 * @date 2020-12-9 11:22:08
 */
@RestController
@RequestMapping("api/list")
public class ListApiController {
    //  使用api ElasticsearchRestTemplate 来执行创建index，type
    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private SearchService searchService;

    //  访问这个控制器的时候，自动创建index,type
    @GetMapping("inner/createIndex")
    public Result createIndex(){
        //  执行方法
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);

        return Result.ok();
    }

    /**
     * 上架商品
     * @param skuId
     * @return
     */
    @GetMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    /**
     * 下架商品
     * @param skuId
     * @return
     */
    @GetMapping("inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     * 热度排名远程调用接口
     * @param skuId
     * @return
     */
    @GetMapping("inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId) {
        // 调用服务层
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

}
