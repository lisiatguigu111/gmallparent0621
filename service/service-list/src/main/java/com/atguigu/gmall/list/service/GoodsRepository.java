package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author mqx
 * @date 2020-12-9 11:39:45
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
