package com.qiuzhitech.onlineshopping_07.service.mq;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RocketMQServiceTest {

    @Resource
    private RocketMQService rocketMQService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void sendMessage() {
        int i = 1;
        while (i > 0) {
            i--;
            rocketMQService.sendMessage("consumerTopic", "message" + i + ": Today is " + new Date());
//        String topic = "consumerTopic";
//        String message = "Test message at " + new Date();
//        rocketMQService.sendMessage(topic, message);
        }

    }
}