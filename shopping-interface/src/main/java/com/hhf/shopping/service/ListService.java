package com.hhf.shopping.service;

import com.hhf.shopping.bean.SkuLsInfo;
import com.hhf.shopping.bean.SkuLsParams;
import com.hhf.shopping.bean.SkuLsResult;


public interface ListService {
    /**
     * 保存数据到es中
     */
    void saveSkuInfo(SkuLsInfo skuLsInfo);

    /**
     *用户根据条件检索相关的数据
     */
    SkuLsResult search(SkuLsParams skuLsParams);
}

