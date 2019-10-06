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

    /**
     * 根据三级分类属性获取spu
     * @return
     */
    List<SpuInfo> getSpuList(SpuInfo spuInfo);

    /**
     *
     * 查询基本销售属性表
     */
    List<BaseSaleAttr> getBaseSaleAttrList();


    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 保存spu的图片
     */
    List<SpuImage> getSpuImageList(SpuImage spuImage);

    /**
     * 根据id获取销售属性
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    /**
     * 保存商品属性spu管理的商品信息管理sku名称到商品规格描述的数据
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     *根据sku的id去查询skuinfo
     */
    SkuInfo getSkuInfo(String skuId);

    /**
     *根据sku的id去查询skuimage图片列表
     */
    List<SkuImage> getSkuImageBySkuId(String skuId);

    /**
     *根据sku的id和spu的id去查询销售属性
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /**
     *根据spuid去查询销售属性值id，实现前台详情页的销售属性值的跳转
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);
}
