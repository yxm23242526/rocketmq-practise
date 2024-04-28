package com.example.mqb.listener;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "bootTestTopic", consumerGroup = "boot-test-consumer-group")
public class SimpleBootMsgListener implements RocketMQListener<MessageExt> {


    /**
     * 这个方法就是消费者的方法
     * 如果泛型是固定的类型，那么消息体就是我们的参数 String message
     * MessageExt类型是消息的所有内容
     *
     *  没有报错就是签收了
     * 报错就是拒收就会重试
     * @param message
     */
    @Override
    public void onMessage(MessageExt message) {
        System.out.println(new String(message.getBody()));
    }
}
