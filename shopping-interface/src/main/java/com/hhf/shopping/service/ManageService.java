package com.hhf.shopping.service;

import com.hhf.shopping.bean.*;

import java.util.List;

public interface ManageService {

    /**
     * 查询所有一级分类数据
     */
    List<BaseCatalog1> getCatalog1();

    /**
     * 根据一级分类id去查询二级分类数据
     */
    List<BaseCatalog2> getCatalog2(String catalog1Id);

    /**
     * 根据二级分类id去查询三级分类数据
     */
    List<BaseCatalog3> getCatalog3(String catalog2Id);

    /**
     *根据三级分类id去查询平台属性
     */
    List<BaseAttrInfo> getAttrList(String catalog3Id);

    /**
     *添加平台属性
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 平台属性id去查询平台属性值
     */
    List<BaseAttrValue> getAttrValueList(String attrId);

    /**
     *平台属性id查询平台属性对象
     */
    BaseAttrInfo getAttrInfo(String attrId);
}
