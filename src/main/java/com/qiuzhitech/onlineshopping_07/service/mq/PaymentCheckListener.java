package com.qiuzhitech.onlineshopping_07.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_07.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_07.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_07.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.LockSupport;

@Component
@Slf4j
@RocketMQMessageListener(topic = "paymentCheck", consumerGroup ="paymentCheckGroup")
public class PaymentCheckListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    RedisService redisService;
    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());

        // convert message to order
        OnlineShoppingOrder orderFromMessage = JSON.parseObject(message, OnlineShoppingOrder.class);
        // check on db to see payment status
        OnlineShoppingOrder order = onlineShoppingOrderDao.queryOrderByOrderNum(orderFromMessage.getOrderNo());
        if(order == null){
            throw new RuntimeException("order not found");
        }
        //do nothing if user already paid
        int orderStatus = order.getOrderStatus();
        if(orderStatus  == 2){
            log.info("order have been paid");
        }else{
            //not paying over time, revert commodity stock and update Order Status to 99
            log.info("you did not finish your payment, order number is: {} ", order.getOrderNo());
            order.setOrderStatus(99);
            //update into database
            onlineShoppingOrderDao.updateOrder(order);
            onlineShoppingCommodityDao.revertStockWithCommodityId(order.getCommodityId());

//            你在 onlineShoppingOrderDao.updateOrder(order) 里更新了订单状态到 数据库。
//            但是库存数据很可能是 走缓存（Redis）来做快速读取），尤其是高并发秒杀/购物系统。
//            如果只更新数据库，不更新 Redis，那么 Redis 里的库存值可能还是旧的，就会出现数据不一致问题。
//            例如：
//            用户下单 → Redis 扣库存。
//            超时未支付 → 数据库库存加回去了，但 Redis 没加回去。
//            结果就是 Redis 里库存少了，别人买不了，但数据库其实有库存。

            String redisKey = "online_shopping:online_shopping_commodity:stock:" + order.getCommodityId();
            Long currentStockCount = redisService.revertStockWithCommodityId(redisKey);
            redisService.removeFromDenyList(order.getUserId().toString(), order.getCommodityId().toString());
            log.info("Redis revert commodityId {}, current Available Count：{}",
                    order.getCommodityId(),
                    currentStockCount);
        }
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

