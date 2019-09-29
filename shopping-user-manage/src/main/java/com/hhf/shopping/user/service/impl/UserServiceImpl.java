package com.hhf.shopping.user.service.impl;

import com.hhf.shopping.bean.UserInfo;
import com.hhf.shopping.service.UserService;
import com.hhf.shopping.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }
}
