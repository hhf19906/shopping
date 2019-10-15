package com.hhf.shopping.service;


import com.hhf.shopping.bean.OrderInfo;

public interface OrderService {
    //保存相应的订单
    String saveOrder(OrderInfo orderInfo);

    //验证流水号
    boolean checkTradeCode(String userId, String tradeCodeNo);

    //删除流水号
    void delTradeCode(String userId);

    //生成流水号
    String getTradeNo(String userId);

    //验证库存
    boolean checkStock(String skuId, Integer skuNum);



    OrderInfo getOrderInfo(String orderId);
}
