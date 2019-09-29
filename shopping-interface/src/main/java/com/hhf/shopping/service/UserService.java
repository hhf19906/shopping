package com.hhf.shopping.service;

import com.hhf.shopping.bean.UserAddress;
import com.hhf.shopping.bean.UserInfo;

import java.util.List;

/**
 * Created by Administrator on 2019/9/29.
 */
public interface UserService {

    List<UserInfo> findAll();

    //根据用户id查询用户地址列表
    public List<UserAddress> getUserAddressList(String userId);


}
