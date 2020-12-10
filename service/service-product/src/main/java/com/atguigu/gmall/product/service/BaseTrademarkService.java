package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

//继承一个IService 接口
public interface BaseTrademarkService extends IService<BaseTrademark> {

    /**
     * 获取品牌列表
     * @param baseTrademarkPage
     * @return
     */
    IPage getBaseTradeMarkList(Page<BaseTrademark> baseTrademarkPage);
}
