package com.example.mqb.listener;

import com.alibaba.fastjson.JSON;
import com.example.mqb.domain.MsgModel;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(topic = "bootOrderlyTopic",
        consumerGroup = "boot-orderly-consumer-group",
        consumeMode = ConsumeMode.ORDERLY,
        maxReconsumeTimes = 5)
public class OrderlyMsgListener implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt message) {
        MsgModel msgModel = JSON.parseObject(new String(message.getBody()), MsgModel.class);
        System.out.println(msgModel);
    }
}
