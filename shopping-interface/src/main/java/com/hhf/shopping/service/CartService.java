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
}
