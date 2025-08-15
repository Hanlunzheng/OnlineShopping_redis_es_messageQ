package com.qiuzhitech.onlineshopping_07.service.mq;


import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.LockSupport;

@Component
@Slf4j
@RocketMQMessageListener(topic = "consumerTopic", consumerGroup ="consumerGroup")
public class TestConsumerListener implements RocketMQListener<MessageExt> , RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(MessageExt messageExt) {
//        String body = new String(messageExt.getBody());

//        log.info("Receive message: {}", body);
        if (messageExt.getReconsumeTimes() <= 1) {
            long nanos = 1_000_000_000L; // 3 seconds in nanoseconds
            LockSupport.parkNanos(nanos);
            log.info("receive message: {}", messageExt);
            throw new RuntimeException("test");
        } else {

            log.info("this is " + messageExt.getReconsumeTimes() + " times");
            log.info("Received success message" + messageExt);
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
