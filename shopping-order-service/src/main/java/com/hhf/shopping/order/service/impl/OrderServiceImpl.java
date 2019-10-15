package com.hhf.shopping.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hhf.shopping.bean.OrderDetail;
import com.hhf.shopping.bean.OrderInfo;
import com.hhf.shopping.bean.enums.OrderStatus;
import com.hhf.shopping.bean.enums.ProcessStatus;
import com.hhf.shopping.config.RedisUtil;
import com.hhf.shopping.order.mapper.OrderDetailMapper;
import com.hhf.shopping.order.mapper.OrderInfoMapper;
import com.hhf.shopping.service.OrderService;
import com.hhf.shopping.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;


    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional
    public String saveOrder(OrderInfo orderInfo) {

        // 数据不完整！总金额，订单状态，第三方交易编号，创建时间，过期时间，进程状态
        // 总金额
        orderInfo.sumTotalAmount();
        // 创建时间
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        // 第三方交易编号
        String outTradeNo = "hhf" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        // 创建时间
        orderInfo.setCreateTime(new Date());
        // 过期时间 +1
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());

        // 进程的状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);
        // 只保存了一份订单
        orderInfoMapper.insertSelective(orderInfo);

        // 订单的明细
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {

            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);
        }

        return orderInfo.getId();
    }


    public String getTradeNo(String userId) {

        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String tradeNoKey = "user:" + userId + ":tradeCode";
        // 定义一个流水号
        String tradeNo = UUID.randomUUID().toString();
        jedis.set(tradeNoKey, tradeNo);
        jedis.close();

        return tradeNo;
    }




    public boolean checkTradeCode(String userId, String tradeCodeNo) {
        //获取缓存的流水号
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String tradeNoKey = "user:"+userId+":tradeCode";
        // 获取数据
        String tradeNo = jedis.get(tradeNoKey);
        // 关闭
        jedis.close();
        return tradeCodeNo.equals(tradeNo);
    }


    public void delTradeCode(String userId) {
        // 获取jedis
        Jedis jedis = redisUtil.getJedis();
        // 定义key
        String tradeNoKey = "user:"+userId+":tradeCode";
        // 删除
        jedis.del(tradeNoKey);
        jedis.close();


    }

    @Override
    public boolean checkStock(String skuId, Integer skuNum) {
        String result = HttpClientUtil.doGet("http://localhost:9001/hasStock?skuId=" + skuId + "&num=" + skuNum);

        return "1".equals(result);


    }

    @Override
    public OrderInfo getOrderInfo(String orderId) {
        return orderInfoMapper.selectByPrimaryKey(orderId);
    }




}
