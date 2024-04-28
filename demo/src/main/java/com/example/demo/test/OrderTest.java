package com.example.demo.test;

import com.example.demo.consts.MQConsts;
import com.example.demo.domain.MsgModel;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderTest {


    private List<MsgModel> msgModels = Arrays.asList(
            new MsgModel("qwer", 1, "下单"),
            new MsgModel("qwer", 1, "短信"),
            new MsgModel("qwer", 1, "物流"),

            new MsgModel("zxcv", 2, "下单"),
            new MsgModel("zxcv", 2, "短信"),
            new MsgModel("zxcv", 2, "物流")
    );

    @Test
    public void orderProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("order_producer_group");
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        producer.start();
        msgModels.forEach(msgModel -> {
            Message message = new Message("orderTopic", msgModel.toString().getBytes());
            try {
                producer.send(message, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                        int hash = arg.toString().hashCode();
                        int i = hash % mqs.size();
                        return mqs.get(i);
                    }
                }, msgModel.getOrderId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //发延迟消息
        producer.shutdown();
        System.out.println("发送成功");
    }

    //这个不知道为什么跑不成功
    @Test
    public void orderConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("order_consumer_group");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("orderTopic", "*");
        //放到一个queue中
        consumer.registerMessageListener(new MessageListenerOrderly() { //顺序模式 单线程
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                System.out.println("线程ID" + Thread.currentThread().getId());
                System.out.println(new String(msgs.get(0).getBody()));
                return ConsumeOrderlyStatus.SUCCESS ;
            }
        });
        consumer.start();
        System.in.read();
    }
}
