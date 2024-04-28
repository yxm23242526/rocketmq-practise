package com.example.mqb.listener;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "modeTopic",
        consumerGroup = "mode-consumer-group-a",
        messageModel = MessageModel.CLUSTERING)
public class C3 implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("我是mode-consumer-group-a组的第3个消费者：" + message);
    }
}
