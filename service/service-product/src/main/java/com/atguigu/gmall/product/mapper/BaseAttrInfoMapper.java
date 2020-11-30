package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    /**
     * 根据分类id查询品台属性+平台属性值
     * 在mybatis传递多个参数的时候，需要添加注解后台才能获取到.
     * @param category1Id
     * @param category2Id
     * @param category3Id
     */
  List<BaseAttrInfo> selectBaseAttrInfoList(@Param("category1Id") Long category1Id,
                                            @Param("category2Id") Long category2Id,
                                            @Param("category3Id") Long category3Id);
}
