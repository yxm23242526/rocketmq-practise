package com.example.mqb.listener;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "modeTopic",
        consumerGroup = "mode-consumer-group-b",
        messageModel = MessageModel.BROADCASTING) //广播模式
public class C4 implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("我是mode-consumer-group-b组的第1个消费者：" + message);
    }
}
