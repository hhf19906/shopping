package com.hhf.shopping.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hhf.shopping.bean.CartInfo;
import com.hhf.shopping.bean.SkuInfo;
import com.hhf.shopping.cart.constant.CartConst;
import com.hhf.shopping.cart.mapper.CartInfoMapper;
import com.hhf.shopping.config.RedisUtil;
import com.hhf.shopping.service.CartService;
import com.hhf.shopping.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartInfoMapper cartInfoMapper;

    @Reference
    ManageService manageService;

    @Autowired
    RedisUtil redisUtil;

    //登录后添加购物车
    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;

        //查询购物车中是否有相同的商品，若有就数量相加
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        cartInfo.setSkuId(skuId);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
        if (cartInfoExist!=null){ //可能有相同的商品
            //数量进行相加
            cartInfoExist.setSkuNum(cartInfoExist.getSkuNum()+skuNum);
            cartInfoExist.setSkuPrice(cartInfoExist.getCartPrice());
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);

        }else {
            //没有相同的商品
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo1 = new CartInfo();
            cartInfo1.setSkuId(skuId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setUserId(userId);
            cartInfo1.setSkuNum(skuNum);

            //添加到数据库
            cartInfoMapper.insertSelective(cartInfo1);
            cartInfoExist = cartInfo1;
        }
        //放入缓存
        jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfoExist));

       // 设置购物车的过期时间
//        String userInfoKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USERINFOKEY_SUFFIX;
//        Long ttl = jedis.ttl(userInfoKey);
//        jedis.expire(cartKey,ttl.intValue());
        jedis.close();

    }

    @Override
    public List<CartInfo> getCartList(String userId) {
        List<CartInfo> cartInfoList= new ArrayList<>();

        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        List<String> stringList = jedis.hvals(cartKey);
        if (stringList!=null &&stringList.size()>0){
            for (String cartInfoStr : stringList) {
                cartInfoList.add(JSON.parseObject(cartInfoStr,CartInfo.class));
            }
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {

                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        }else {
            cartInfoList = loadCartCache(userId);
            return cartInfoList;
        }

    }

    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId) {
        // 根据userId 获取购物车数据
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        // 开始合并 合并条件：skuId要相同
        for (CartInfo cartInfoCK : cartListCK) {

            boolean isMatch =false;
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if (cartInfoCK.getSkuId().equals(cartInfoDB.getSkuId())){
                    // 将数量进行相加
                    cartInfoDB.setSkuNum(cartInfoCK.getSkuNum()+cartInfoDB.getSkuNum());
                    // 修改数据库
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                    isMatch=true;
                }
            }
            // 没有匹配上
            if (!isMatch){
                // 未登录的对象添加到数据库
                // 将用户Id 赋值给未登录对象
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }
        // 最终将合并之后的数据返回！
        List<CartInfo> cartInfoList = loadCartCache(userId);

        //勾选下订单的并和未登录的合并
        for (CartInfo cartInfoDB : cartInfoList) {
            for (CartInfo cartInfoCK : cartListCK) {
                if (cartInfoDB.getSkuId().equals(cartInfoCK.getSkuId())){
                    if ("1".equals(cartInfoCK.getIsChecked())){
                        //修改数据库的状态
                        cartInfoDB.setIsChecked(cartInfoCK.getIsChecked());
                        checkCart(cartInfoDB.getSkuId(),"1",userId);
                    }
                }
            }
        }
        
        return cartInfoList;
    }

    @Override
    public void checkCart(String skuId, String isChecked, String userId) {
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;
        String cartInfoJson = jedis.hget(cartKey, skuId);
        CartInfo cartInfo = JSON.parseObject(cartInfoJson, CartInfo.class);
        //从页面中获取商品状态
        cartInfo.setIsChecked(isChecked);
        //放入购物车
        jedis.hset(cartKey,skuId, JSON.toJSONString(cartInfo));

        String cartKeyChecked = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
        if ("1".equals(isChecked)){ //勾选商品
            jedis.hset(cartKeyChecked,skuId,JSON.toJSONString(cartInfo));
        }else {  //没有勾选商品
            jedis.hdel(cartKeyChecked,skuId);
        }
        jedis.close();


    }


    @Override
    public List<CartInfo> getCartCheckedList(String userId) {

        List<CartInfo> cartInfoList = new ArrayList<>();


        Jedis jedis = redisUtil.getJedis();
        // 被选中的购物车
        String cartKeyChecked = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CHECKED_KEY_SUFFIX;

        List<String> stringList = jedis.hvals(cartKeyChecked);
        // 循环判断
        if (stringList!=null && stringList.size()>0){
            for (String cartJson : stringList) {
                cartInfoList.add(JSON.parseObject(cartJson,CartInfo.class));
            }

        }

        jedis.close();
        return cartInfoList;

    }


    public List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList==null || cartInfoList.size()==0){
            return null;
        }
        Jedis jedis = redisUtil.getJedis();
        String cartKey = CartConst.USER_KEY_PREFIX+userId+CartConst.USER_CART_KEY_SUFFIX;

        HashMap<String,String> map =new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            map.put(cartInfo.getSkuId(),JSON.toJSONString(cartInfo));
        }
        jedis.hmset(cartKey,map);
        jedis.close();
        return cartInfoList;
    }
}
