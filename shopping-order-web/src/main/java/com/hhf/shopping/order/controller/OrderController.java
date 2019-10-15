package com.hhf.shopping.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hhf.shopping.bean.*;
import com.hhf.shopping.config.LoginRequire;
import com.hhf.shopping.service.CartService;
import com.hhf.shopping.service.ManageService;
import com.hhf.shopping.service.OrderService;
import com.hhf.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    UserService userService;

    @Reference
    CartService cartService;

    @Reference
    OrderService orderService;

    @Reference
    ManageService manageService;

    //订单结算页
    @RequestMapping("trade")
    @LoginRequire
    public String trade(HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        //拿到地址
        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        request.setAttribute("userAddressList",userAddressList);

        //显示相应的送货清单
        List<CartInfo> cartInfoList = cartService.getCartCheckedList(userId);
        ArrayList<OrderDetail> orderDetailArrayList = new ArrayList<>();

        for (CartInfo cartInfo : cartInfoList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetailArrayList.add(orderDetail);
        }

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailArrayList);
        orderInfo.sumTotalAmount();//计算总金额方法
        request.setAttribute("totalAmount",orderInfo.getTotalAmount());

        request.setAttribute("orderDetailArrayList",orderDetailArrayList);

        String tradeNo = orderService.getTradeNo(userId);
        request.setAttribute("tradeNo",tradeNo);
        return "trade";
    }

    //提交订单的页面处理
    @RequestMapping("submitOrder")
    @LoginRequire
    public String submitOrder(OrderInfo orderInfo,HttpServletRequest request){

        String userId = (String) request.getAttribute("userId");
        orderInfo.setUserId(userId);

        // 判断是否是重复提交
        // 先获取页面的流水号s数据
        String tradeNo = request.getParameter("tradeNo");
        // 调用比较的方法
        boolean result = orderService.checkTradeCode(userId, tradeNo);
        // 是重复提交
        if (!result){
            request.setAttribute("errMsg","订单已提交，不能重复提交！");
            return "tradeFail";
        }

        //查询商品库存
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            boolean flag = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!flag) {
                request.setAttribute("errMsg", orderDetail.getSkuName() + "商品库存不足！");
                return "tradeFail";
            }

            SkuInfo skuInfo = manageService.getSkuInfo(orderDetail.getSkuId());
            int res = skuInfo.getPrice().compareTo(orderDetail.getOrderPrice());
            if (res != 0) {
                request.setAttribute("errMsg", orderDetail.getSkuName() + "价格不匹配");
                cartService.loadCartCache(userId);
                return "tradeFail";
            }
        }

        String orderId = orderService.saveOrder(orderInfo);

        // 删除流水号
        orderService.delTradeCode(userId);

        // 支付
        return "redirect://localhost:8092/index?orderId="+orderId;
    }
    }
