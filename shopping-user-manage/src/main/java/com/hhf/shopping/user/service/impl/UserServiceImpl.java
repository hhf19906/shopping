package com.hhf.shopping.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hhf.shopping.bean.UserAddress;
import com.hhf.shopping.bean.UserInfo;
import com.hhf.shopping.service.UserService;
import com.hhf.shopping.user.mapper.UserAddressMapper;
import com.hhf.shopping.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        return userAddressMapper.select(userAddress);

    }
}
