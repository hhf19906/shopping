package com.hhf.shopping.manage.mapper;

import com.hhf.shopping.bean.SpuSaleAttr;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpuSaleAttrMapper  extends Mapper<SpuSaleAttr> {
    //根据id去查询销售属性
    List<SpuSaleAttr> selectSpuSaleAttrList(String spuId);
}
