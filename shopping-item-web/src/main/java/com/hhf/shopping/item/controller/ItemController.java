package com.hhf.shopping.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.hhf.shopping.bean.*;
import com.hhf.shopping.config.LoginRequire;
import com.hhf.shopping.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
@CrossOrigin
public class ItemController {


    @Reference
    ManageService manageService;

    @RequestMapping("{skuId}.html")
    @LoginRequire //用户在访问商品详情时，必须登录才能可以
    public String item(@PathVariable String skuId, HttpServletRequest request){
        // 根据skuId 获取数据
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        // 查询销售属性，销售属性值集合 spuId，skuId
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);

        // 获取销售属性值Id
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());

        // 遍历集合拼接字符串
        String key = "";
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            if (key.length()>0){
                key+="|";
            }
            key+= skuSaleAttrValue.getSaleAttrValueId();
            if ((i+1)== skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals( skuSaleAttrValueList.get(i+1).getSkuId())){
                // 放入map集合
                map.put(key,skuSaleAttrValue.getSkuId());
                // 并且清空key
                key="";
            }
        }
        // 将map 转换为json 字符串
        String valuesSkuJson  = JSON.toJSONString(map);
        System.out.println("拼接Json：="+valuesSkuJson );

        request.setAttribute("valuesSkuJson",valuesSkuJson);
        request.setAttribute("spuSaleAttrList",spuSaleAttrList);
        request.setAttribute("skuInfo",skuInfo);
        return "item";
    }
}
