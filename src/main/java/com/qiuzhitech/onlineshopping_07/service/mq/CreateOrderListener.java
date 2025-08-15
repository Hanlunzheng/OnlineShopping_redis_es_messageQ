package com.qiuzhitech.onlineshopping_07.service.mq;


import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.LockSupport;


@Component
@Slf4j
@RocketMQMessageListener(topic = "createOrder", consumerGroup ="createOrderGroup")
public class CreateOrderListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Autowired
    private RocketMQService rocketMQService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        //convert message to order(follow placeOrderOneQql logic )
       OnlineShoppingOrder order = JSON.parseObject(message, OnlineShoppingOrder.class);
        Long commodityId = order.getCommodityId();
        Long userId = order.getUserId();
        int result = onlineShoppingCommodityDao.deductStockWithCommodityId(commodityId);
        // insert into commodity table
        if(result > 0 ) {
            onlineShoppingOrderDao.insertOrder(order);
            rocketMQService.sendDelayMessage("paymentCheck", JSON.toJSONString(order), 2);
        }


        //insert into order table


    }


    @Override
    public void prepareStart (DefaultMQPushConsumer consumer){
// 在此方法中可以设置一些消费者的属性
        consumer.setMaxReconsumeTimes(3); // 设置最大重试次数
        consumer.setConsumeTimeout(5000); // 设置消费超时时间为 5 秒
        consumer.setConsumeThreadMin(2);
        consumer.setConsumeThreadMax(2);


    }
}

