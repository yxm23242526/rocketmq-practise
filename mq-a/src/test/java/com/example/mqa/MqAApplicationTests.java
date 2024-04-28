package com.example.mqa;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MqAApplicationTests {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void contextLoads() {
        rocketMQTemplate.syncSend("bootTestTopic", "我是boot的一个消息");
    }

}
