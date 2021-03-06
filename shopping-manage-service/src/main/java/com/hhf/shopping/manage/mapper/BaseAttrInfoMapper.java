package com.hhf.shopping.manage.mapper;

import com.hhf.shopping.bean.BaseAttrInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo>{
    /**
     * 根据三级分类id查询平台属性集合
     */
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(String catalog3Id);

}
