package com.hhf.shopping.service;

import com.hhf.shopping.bean.CartInfo;

import java.util.List;

/**
 * Created by Administrator on 2019/10/11.
 */
public interface CartService {

    void  addToCart(String skuId,String userId,Integer skuNum);

    //根据用户的id去查询购物车数据
    List<CartInfo> getCartList(String userId);

    //合并购物车
    List<CartInfo> mergeToCartList(List<CartInfo> cartListCK, String userId);

    //修改登录后的购物车商品状态
    void checkCart(String skuId, String isChecked, String userId);

    //查询购物车列表数据
    List<CartInfo> getCartCheckedList(String userId);

    //查询实时的价格
    List<CartInfo> loadCartCache(String userId);
}
