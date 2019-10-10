package com.hhf.shopping.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hhf.shopping.bean.SkuInfo;
import com.hhf.shopping.bean.SkuLsInfo;
import com.hhf.shopping.bean.SpuImage;
import com.hhf.shopping.bean.SpuSaleAttr;
import com.hhf.shopping.service.ListService;
import com.hhf.shopping.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {

    @Reference
    ListService listService;

    @Reference
    ManageService manageService;

    @RequestMapping("spuImageList")
    public List<SpuImage> spuImageList(SpuImage spuImage) {
        return manageService.getSpuImageList(spuImage);
    }

    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> spuSaleAttrList(String spuId) {
        return manageService.getSpuSaleAttrList(spuId);
    }

    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        if (skuInfo != null) {
            manageService.saveSkuInfo(skuInfo);
        }

    }

    //上传一个商品，批量上传
    @RequestMapping("onSale")
    public void onSale(String skuId) {
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        // 属性拷贝
        BeanUtils.copyProperties(skuInfo, skuLsInfo);
//        try {
//            BeanUtils.copyProperties(skuLsInfo, skuInfo);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
        listService.saveSkuInfo(skuLsInfo);

    }
}
