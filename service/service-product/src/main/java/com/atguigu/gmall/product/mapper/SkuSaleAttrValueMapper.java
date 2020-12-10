package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 根据spuId获取spu对应的销售属性值组成的skuId集合
     * @param spuId
     * @return
     */
    List<Map> selectSkuValueIdsMap(Long spuId);
}