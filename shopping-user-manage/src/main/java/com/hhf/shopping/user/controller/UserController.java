package com.hhf.shopping.user.controller;

import com.hhf.shopping.bean.UserInfo;
import com.hhf.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("findAll")
    public List<UserInfo> findAll(){
       return userService.findAll();
    }
}
