package com.hhf.shopping.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.hhf.shopping.bean.OrderInfo;
import com.hhf.shopping.bean.PaymentInfo;
import com.hhf.shopping.bean.enums.PaymentStatus;
import com.hhf.shopping.payment.config.AlipayConfig;
import com.hhf.shopping.service.OrderService;
import com.hhf.shopping.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class PaymentController {

    @Reference
    OrderService orderService;

    @Reference
    PaymentService paymentService;

    @Autowired
    private AlipayClient alipayClient;

    //订单支付选择页
    @RequestMapping("index")
    public String index(String orderId, HttpServletRequest request){

        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        request.setAttribute("orderId",orderId); //保存订单id
       request.setAttribute("totalAmount",orderInfo.getTotalAmount());  //保存订单总金额
        return "index";
    }

    //生成支付的二维码
    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipaySubmit(HttpServletRequest request, HttpServletResponse response){
        String orderId = request.getParameter("orderId");
        OrderInfo orderInfo = orderService.getOrderInfo(orderId);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(orderId);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        paymentInfo.setSubject("买手机");
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        paymentInfo.setCreateTime(new Date());
        paymentService.savePaymentInfo(paymentInfo);
        //生成相应的支付二维码
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url); //同步回调
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url); //异步回调

        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no",paymentInfo.getOutTradeNo());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount",paymentInfo.getTotalAmount());
        map.put("subject",paymentInfo.getSubject());

        alipayRequest.setBizContent(JSON.toJSONString(map));
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        response.setContentType("text/html;charset=UTF-8");
        return form;
    }

    //成功支付后的页面
    @RequestMapping("alipay/callback/return")
    public String callbackReturn(){
        return "redirect:"+AlipayConfig.return_order_url;
    }

    // 异步回调
    @RequestMapping("alipay/callback/notify")
    public String callbackNotify(@RequestParam Map<String,String> paramMap, HttpServletRequest request){
      //将异步通知中收到的所有参数都存放到map中
        boolean flag = false; //调用SDK验证签名
        try {
            flag = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(flag){
            String trade_status = paramMap.get("trade_status");
            //查询支付状态记录
            String out_trade_no = paramMap.get("out_trade_no");

            if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)){
                // 当前的订单支付状态如果是已经付款，或者是关闭
                PaymentInfo paymentInfoQuery = new PaymentInfo();
                paymentInfoQuery.setOutTradeNo(out_trade_no);
                PaymentInfo paymentInfo = paymentService.getPaymentInfo(paymentInfoQuery);

                if (paymentInfo.getPaymentStatus()==PaymentStatus.PAID || paymentInfo.getPaymentStatus()==PaymentStatus.ClOSED){
                    return "failure";
                }

                // 更新交易记录的状态
                PaymentInfo paymentInfoUPD = new PaymentInfo();
                paymentInfoUPD.setPaymentStatus(PaymentStatus.PAID);
                paymentInfoUPD.setCallbackTime(new Date());

                paymentService.updatePaymentInfo(out_trade_no,paymentInfoUPD);
                return "success";
            }
        }else{
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }
        return "failure";
    }


    //微信扫码支付
    @RequestMapping("wx/submit")
    @ResponseBody
    public Map wxSubmit(String orderId) {
        orderId = UUID.randomUUID().toString().replace("-", "");
        Map map = paymentService.createNative(orderId, "1");
        System.out.println(map.get("code_url"));
        return map;
    }
}
