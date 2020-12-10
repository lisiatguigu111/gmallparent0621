package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * select *
     * from spu_sale_attr ssa
     *     inner join spu_sale_attr_value ssav
     * on ssa.spu_id=ssav.spu_id and ssa.base_sale_attr_id=ssav.base_sale_attr_id
     * where ssa.spu_id=15;
     * 多表关联查询
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> selectSpuSaleAttrList(long spuId);

    /**
     * 回显销售属性+销售属性值+锁定
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> spuSaleAttrListCheckBySku(@Param("skuId") Long skuId, @Param("spuId")Long spuId);
}
