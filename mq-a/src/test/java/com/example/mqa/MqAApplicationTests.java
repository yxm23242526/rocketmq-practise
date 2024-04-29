package com.example.mqa;

import com.alibaba.fastjson.JSON;
import com.example.mqa.domain.MsgModel;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MqAApplicationTests {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void contextLoads() {
        rocketMQTemplate.syncSend("bootTestTopic", "我是boot的一个消息");

        //异步
        rocketMQTemplate.asyncSend("bootTestTopic", "我是boot的一个异步消息", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable e) {
                System.out.println("发送失败" + e.getMessage());
            }
        });

        //单向消息
        rocketMQTemplate.sendOneWay("bootOnewayTopic", "单向消息");

        //延迟消息
        Message<String> msg = MessageBuilder.withPayload("我是一个延迟消息").build();
        rocketMQTemplate.syncSend("bootDelayTopic", msg, 3000, 3);

        //顺序消息
        List<MsgModel> msgModels = Arrays.asList(
                new MsgModel("qwer", 1, "下单"),
                new MsgModel("qwer", 1, "短信"),
                new MsgModel("qwer", 1, "物流"),

                new MsgModel("zxcv", 2, "下单"),
                new MsgModel("zxcv", 2, "短信"),
                new MsgModel("zxcv", 2, "物流")
        );
        msgModels.forEach(msgModel -> {
            rocketMQTemplate.syncSendOrderly("bootOrderlyTopic", JSON.toJSONString(msgModel),
                    msgModel.getOrderId());
        });
    }


    @Test
    void tagKeyTest() throws Exception{
        //topic:tag
        rocketMQTemplate.syncSend("bootTagTopic:tagA","我是一个带tag的消息");

        //key是携带在消息头的
        Message<String> message = MessageBuilder.withPayload("我是一个带key的消息")
                .setHeader(RocketMQHeaders.KEYS, "一个Key").build();
        rocketMQTemplate.syncSend("bootKeyTopic", message);
    }


    @Test
    void modeTest() throws Exception{
        for (int i = 0; i < 10; i++){
            rocketMQTemplate.syncSend("modeTopic", "我是第" + i + "个消息");
        }
    }

    //积压问题
    @Test
    void overStockTest() throws Exception{
        for (int i = 0; i < 100000; i++){
            Thread.sleep(500L);
            rocketMQTemplate.syncSend("overStockTopic", "我是第" + i + "个积压问题");
        }
    }
}
