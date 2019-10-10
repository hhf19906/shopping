package com.hhf.shopping.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.hhf.shopping.bean.SkuLsParams;
import com.hhf.shopping.bean.SkuLsResult;
import com.hhf.shopping.service.ListService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListController {
    @Reference
    ListService listService;

    @RequestMapping("list.html")
    @ResponseBody
    public String listData(SkuLsParams skuLsParams){
        SkuLsResult search = listService.search(skuLsParams);
        return JSON.toJSONString(search);
    }
}
