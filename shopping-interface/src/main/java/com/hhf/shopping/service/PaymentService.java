package com.hhf.shopping.service;

import com.hhf.shopping.bean.PaymentInfo;

import java.util.Map;


public interface PaymentService {

    //保存交易的记录
    void savePaymentInfo(PaymentInfo paymentInfo);

    PaymentInfo getPaymentInfo(PaymentInfo paymentInfoQuery);

    void updatePaymentInfo(String out_trade_no, PaymentInfo paymentInfoUPD);

    //退款
    boolean refund(String orderId);

    //微信支付
    Map createNative(String orderId, String s);
}
