package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    //注入mapper
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    //查询所有一级分类
    @Override
    public List<BaseCategory1> findAll() {

        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 根据一级分类id查询所有二级分类数据
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        //select * from base_category2 where category1_id =
      return   baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id",category1Id));

    }

    /**
     * 根据二级分类id查询所有三级分类数据
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
     //select * from base_category3 where category2_id = category2Id
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id",category2Id));
    }

    /**
     * 根据分类id查询平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, long category3Id) {

        return  baseAttrInfoMapper.selectBaseAttrInfoList(category1Id,category2Id,category3Id);
    }

    //baseAttrInfo(平台属性) baseAttrV alue(平台属性值) ; 保存这两个数据
    //在这个实现类中既有保存又有修改
    @Override
    @Transactional// 声明式事务
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        if(baseAttrInfo.getId() == null){
            //保存
            //baseAttrInfo 保存平台属性
            baseAttrInfoMapper.insert(baseAttrInfo);
        }else {
            //修改平台属性
            baseAttrInfoMapper.updateById(baseAttrInfo);
        }

        /**
         * baseAttrValue
         * 先删除，再插入数据
         */
        //对应sql语句  delete from base_attr_value where attr_id = baseAttrInfo.getId();
        //弊端：删除完成之后，原始的id不存在了
        QueryWrapper<BaseAttrValue> baseAttrValueQueryWrapper = new QueryWrapper<>();
        baseAttrValueQueryWrapper.eq("attr_id",baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueQueryWrapper);

        //int i = 1/0;//失败
        //保存平台属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if(!CollectionUtils.isEmpty(attrValueList)){
            //循环遍历
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //给attrId 赋值
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }
        }


    }

    //根据平台属性id获取平台属性值集合
    @Override
    public List<BaseAttrValue> getAttrValueList(long attrId) {
//select * from base_attr_value where attr_id = attrId
        return baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id",attrId));

    }




    @Override
    public BaseAttrInfo getBaseAttrInfo(long attrId) {
        //  select * from base_attr_info where id = attrId;
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        if(baseAttrInfo != null){
            //  获取平台属性值集合，将属性值集合放入该对象
            //  select * from base_attr_value where attr_id = attrId;
            baseAttrInfo.setAttrValueList(getAttrValueList(attrId));
        }
        return baseAttrInfo;
    }
}
