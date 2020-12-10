package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private  SkuInfoMapper skuInfoMapper;

    @Autowired
    private  SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;






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

    //baseAttrInfo(平台属性) baseAttrV value(平台属性值) ; 保存这两个数据
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

        //  如下操作 保存
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (!CollectionUtils.isEmpty(attrValueList)){
            //  循环遍历   ,iter : 增强for
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //  给attrId 赋值。 baseAttrInfo.id = baseAttrValue.attrId
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

        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        if(baseAttrInfo != null){
            //  获取平台属性值集合，将属性值集合放入该对象
            //  select * from base_attr_value where attr_id = attrId;
            baseAttrInfo.setAttrValueList(getAttrValueList(attrId));
        }
        return baseAttrInfo;
    }

    @Override
    public IPage getSpuInfoPage(Page<SpuInfo> spuInfoPage, SpuInfo spuInfo) {
        //构建查询条件
        //    http://api.gmall.com/admin/product/ {page}/{limit}?category3Id=61
        //select * from spu_info where category3_id = ? order by id desc
        QueryWrapper<SpuInfo> spuInfoQueryWrapper = new QueryWrapper<>();
        spuInfoQueryWrapper.eq("category3_id",spuInfo.getCategory3Id());
        //设置一个排序规则！  mysql默认的排序规则  asc 倒排
        spuInfoQueryWrapper.orderByDesc("id");
       return spuInfoMapper.selectPage(spuInfoPage,spuInfoQueryWrapper);

    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {

        return baseSaleAttrMapper.selectList(null);
    }

    //保存spu
    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        //商品表
        spuInfoMapper.insert(spuInfo);

        //获取图片列表
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(!CollectionUtils.isEmpty(spuImageList)){
            for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            }
        }

        //销售属性表
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(!CollectionUtils.isEmpty(spuSaleAttrList)){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);


                //在这个销售属性中获取销售属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                //判断
                if(!CollectionUtils.isEmpty(spuSaleAttrValueList)){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }

    }

    @Override
    public List<SpuImage> getSpuImageList(Long spuId) {
        return spuImageMapper.selectList(new QueryWrapper<SpuImage>().eq("spu_id",spuId));
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(long spuId) {

        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    //大保存  sku
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        //sku与平台属性值

        //获取sku与 平台属性值的集合
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(!CollectionUtils.isEmpty(skuAttrValueList)){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
        //sku与销售属性的关系
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(!CollectionUtils.isEmpty(skuSaleAttrValueList)){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                //需要补充的数据
               skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
               skuSaleAttrValue.setSkuId(skuInfo.getId());
               //添加数据
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
        //sku与图片表的关系
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(!CollectionUtils.isEmpty(skuImageList)){
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insert(skuImage);
            }
        }


    }

    @Override
    public IPage<SkuInfo> getSkuInfoList(Page<SkuInfo> skuInfoPage) {
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.orderByDesc("id");
        return skuInfoMapper.selectPage(skuInfoPage,skuInfoQueryWrapper);

    }

    //商品上架
    @Override
    public void onSale(Long skuId) {
        /**
         * sql语句
         * update sku_info set is_sale = 1 where id = skuId;
         */
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setIsSale(1);
        skuInfo.setId(skuId);
        skuInfoMapper.updateById(skuInfo);
    }

    //商品下架
    @Override
    public void cancelSale(Long skuId) {
        /**
         * sql语句 update sku_info set is_sale = 0 where is = skuId;
         */
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setIsSale(0);
        skuInfo.setId(skuId);
        skuInfoMapper.updateById(skuInfo);
    }

    @Override
    @GmallCache(prefix = "getSkuInfo:")
    public SkuInfo getSkuInfoById(Long skuId) {
    //return getSkuInfoByRedis(skuId);
        return getSkuInfoDB(skuId);
    }
    private SkuInfo getSkuInfoByRedis(Long skuId) {
        SkuInfo skuInfo = null;
        try {
            //key=sku:skuId:skuInfo
            String skuKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            //获取数据
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //判断
            if(skuInfo == null){
                //redis中，，没有缓存，查询数据库添加分布式锁
               //声明一个锁的key=sku:skuId:Lock //目的是防止缓存穿透
                String skuKeyLock = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                //声明一个uuid
                String uuid = UUID.randomUUID().toString();
                //开始上锁
                Boolean flag = redisTemplate.opsForValue().setIfAbsent(skuKeyLock, uuid, RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);
                if(flag){
                    //上锁成功，执行业务获取数据库中的数据
                    skuInfo = getSkuInfoDB(skuId);
                    //获取的skuInfo是否为空 防止缓存穿透
                    if(skuInfo == null){
                        SkuInfo skuInfo1 = new SkuInfo();
                        redisTemplate.opsForValue().set(skuKey,skuInfo1,RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                        //等待他过期返回数据
                        return skuInfo1;
                    }
                    //将数据放入缓存
                    redisTemplate.opsForValue().set(skuKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                    //使用lua脚本删除
                    //定义lua脚本
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                    //赋值lua脚本
                    redisScript.setScriptText(script);
                    redisScript.setResultType(Long.class);
                    //删除
                    redisTemplate.execute(redisScript, Arrays.asList(skuKeyLock),uuid);
                    //返回数据
                    return skuInfo;
                }else {
                    //没有获取到锁的线程等待自旋
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //自旋
                    return getSkuInfoDB(skuId);

                }

            }else {
                //说明缓存中有数据
                return skuInfo;
            }
        } catch (Exception e) {
            //如果发生异常  获取异常、、通知运维
            //发送短信通知运维
            e.printStackTrace();
        }
        //如果有异常发生，那么使用数据库兜底
        return getSkuInfoDB(skuId);

    }



    private SkuInfo getSkuInfoDB(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo != null){
            QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
            skuImageQueryWrapper.eq("sku_id",skuId);

            List<SkuImage> skuImages = skuImageMapper.selectList(skuImageQueryWrapper);
            skuInfo.setSkuImageList(skuImages);
        }

        //返回数据
        return skuInfo;
    }

    public SkuInfo getSkuInfoRedisson(Long skuId){
        SkuInfo skuInfo = null;

        try {
            //定义key=sku:skuId:info
            String skuKey = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
            //获取数据
            skuInfo = (SkuInfo) redisTemplate.opsForValue().get(skuKey);
            //判断
            if(skuInfo == null){
                //说明缓存没有数据 从数据库获取
                //声明一个锁的key key=sku:skuId:lock
                String skuKeyLock = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
                //上锁
                RLock lock = redissonClient.getLock(skuKeyLock);
              Boolean res =   lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1,RedisConst.SKULOCK_EXPIRE_PX2,TimeUnit.SECONDS);
                //表示上锁成功
                if(res){
                    try {
                        //业务逻辑
                        //上锁成功 执行业务逻辑
                        skuInfo = getSkuInfoDB(skuId);
                        //获取的skuinfo是否为空
                        if(skuInfo == null){
                            SkuInfo skuInfo1 = new SkuInfo();
                            redisTemplate.opsForValue().set(skuKey,skuInfo1,RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                            //等待他过期
                            //返回数据
                            return skuInfo1;
                        }
                        //将数据放入缓存
                        redisTemplate.opsForValue().set(skuKey,skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                        //表示查询到数据
                        return skuInfo;
                    }  finally {
                        lock.unlock();
                    }
                }else {
                    //没有获取到锁，等待自旋
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //自旋
                   return getSkuInfoDB(skuId);

                }
            }else {
                //如果缓存中有数据
                //直接返回
                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getSkuInfoDB(skuId);
    }

    @Override
    @GmallCache(prefix = "categoryViewByCategory:")
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        //select * from base_category_view where id = 61
        return  baseCategoryViewMapper.selectById(category3Id);

    }

    //根据skuId获取最新价格
    @Override
    @GmallCache(prefix = "skuPrice:")
    public BigDecimal getSkuPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo != null){
            return skuInfo.getPrice();
        }else {
         return    new BigDecimal(0);
        }
    }

    //根据skuId和spuId查询销售属性和销售属性值
    @Override
    @GmallCache(prefix = "spuSaleAttrListCheck:")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {

        return spuSaleAttrMapper.spuSaleAttrListCheckBySku(skuId,spuId);

    }

    @Override
    @GmallCache(prefix = "getSkuValueIdsMap:")
    public Map getSkuValueIdsMap(Long spuId) {
        HashMap<Object, Object> hashMap = new HashMap<>();
        //存储数据
        //通过mapper查询数据并将其放入map集合
       // hashMap.put()
      List<Map> mapList =  skuSaleAttrValueMapper.selectSkuValueIdsMap(spuId);
      if(!CollectionUtils.isEmpty(mapList)){
          for (Map map : mapList) {
              hashMap.put(map.get("value_ids"),map.get("sku_id"));
          }
      }
      //返回数据
        return hashMap;
    }

    @Override
     @GmallCache(prefix = "baseCategoryList")
    public List<JSONObject> getBaseCategoryList() {

        List<JSONObject> list = new ArrayList<>();

        /**
         *1.获取所有分类数据
         * 2根据分类id分组，来获取数据(分类id+分类名字)
         * 3.组装成需要的json数据，添加到list集合
         */
        //获取所有数据
        List<BaseCategoryView> baseCategoryViewList = baseCategoryViewMapper.selectList(null);
        //根据分类Id分组，来获取数据 select * from base_category_view group by category1Id;
        //对应的sql语句：lamuda表达式是下面的select category1_id,bcv.category1_name from base_category_view bcv group by bcv.category1_id,bcv.category1_name;
        //map的key就是一级分类id  value就是一级分类id下所有的数据
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViewList.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //声明一个index变量
        int index = 1;
        //循环遍历
        for (Map.Entry<Long, List<BaseCategoryView>> entry : category1Map.entrySet()) {
            //声明一个对象
            JSONObject category1 = new JSONObject();
            //获取一级分类id
            Long category1Id = entry.getKey();
            //获取一级分类的值
            List<BaseCategoryView> category2List = entry.getValue();

            category1.put("index",index);
            category1.put("categoryId",category1Id);
            //  按照一级分类Id 进行分组了，那么一个组的名称都一样
            category1.put("categoryName",category2List.get(0).getCategory1Name());

            //变量迭代
            index++;
            //声明一个集合来存储二级分类对象数据
            List<JSONObject> category2Child = new ArrayList<>();
            Map<Long, List<BaseCategoryView>> category2Map = category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            //循环遍历
            for (Map.Entry<Long, List<BaseCategoryView>> longListEntry : category2Map.entrySet()) {

                //声明一个对象
                JSONObject category2 = new JSONObject();
                //获取二级分类的id
                Long category2Id = longListEntry.getKey();

                //获取二级分类的value
                List<BaseCategoryView> category3List = longListEntry.getValue();
                //获取二级分类的名称
                String category2Name = category3List.get(0).getCategory2Name();
                //给二级分类对象赋值
                category2.put("categoryId",category2Id);
                category2.put("categoryName",category2Name);

                //添加到集合
                category2Child.add(category2);

                //获取三级分类数据
                //声明一个三级分类集合对象
                List<JSONObject> category3Child = new ArrayList<>();
                //循环遍历三级分类数据集合
                category3List.forEach((baseCategory3View -> {
                    //声明一个三级对象集合
                    JSONObject category3 = new JSONObject();
                    category3.put("categoryId",baseCategory3View.getCategory3Id());
                    category3.put("categoryName",baseCategory3View.getCategory3Name());

                    category3Child.add(category3);
                }));
                //  将三级分类数据添加到二级分类的集合
                category2.put("categoryChild",category3Child);
            }
            //  将每个一级分类添加到集合
            category1.put("categoryChild",category2Child);
            list.add(category1);
        }
        return list;
    }

    //通过品牌Id 来查询数据
    @Override
    public BaseTrademark getTrademarkByTmId(Long tmId) {
        //调用mapper查询数据
        return  baseTrademarkMapper.selectById(tmId);

    }

    //通过skuId 集合来查询数据
    @Override
    public List<BaseAttrInfo> getAttrList(Long skuId) {
        //查询平台属性和平台属性值
        return baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);
    }

}

