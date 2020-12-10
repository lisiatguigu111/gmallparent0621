package com.atguigu.gmall.item.service;

import java.util.Map;

/**
 * @author mqx
 * @date 2020-12-4 14:13:19
 */
public interface ItemService {

    //  数据接口
    Map<String,Object> getBySkuId(Long skuId);
}
