package com.example.mqb.listener;


import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "jiyaTopic",
        consumerGroup = "jiya-consumer-group",
        consumeThreadNumber = 40,
        consumeMode = ConsumeMode.CONCURRENTLY)
public class jiyaListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("我是第一个消费者:" + message);
    }
}
