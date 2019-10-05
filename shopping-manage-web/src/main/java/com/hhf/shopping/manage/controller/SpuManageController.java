package com.hhf.shopping.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hhf.shopping.bean.SpuInfo;
import com.hhf.shopping.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SpuManageController {

    @Reference
    ManageService manageService;

    @RequestMapping("spuList")
    public List<SpuInfo> spuList (SpuInfo spuInfo){
        return manageService.getSpuList(spuInfo);
    }

    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){
        if (spuInfo!=null){
            manageService.saveSpuInfo(spuInfo);
        }

    }
}
