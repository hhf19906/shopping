package com.hhf.shopping.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hhf.shopping.bean.UserAddress;
import com.hhf.shopping.bean.UserInfo;
import com.hhf.shopping.config.RedisUtil;
import com.hhf.shopping.service.UserService;
import com.hhf.shopping.user.mapper.UserAddressMapper;
import com.hhf.shopping.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;


import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    RedisUtil redisUtil;

    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24;

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

    @Override
    public UserInfo login(UserInfo userInfo) {
        String passwd = userInfo.getPasswd();
        //MD5加密密码
        String newPad = DigestUtils.md5DigestAsHex(passwd.getBytes());
        userInfo.setPasswd(newPad);

        UserInfo info = userInfoMapper.selectOne(userInfo);
        //查询数据库是否有当前用户
        if (info!=null){
            //将查询到的用户信息存入到缓存中
            Jedis jedis = redisUtil.getJedis();
            String userKey = userKey_prefix+info.getId()+userinfoKey_suffix;

            jedis.setex(userKey,userKey_timeOut, JSON.toJSONString(info));
            jedis.close();
            return info;
        }

        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String userKey = userKey_prefix+userId+userinfoKey_suffix;
            String userJson = jedis.get(userKey);
            if (!StringUtils.isEmpty(userJson)){
                UserInfo userInfo = JSON.parseObject(userJson, UserInfo.class);
                return userInfo;
        }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
}
