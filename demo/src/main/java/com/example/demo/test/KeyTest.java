package com.example.demo.test;

import com.example.demo.consts.MQConsts;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class KeyTest {

    @Test
    public void keyProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("key_producer_group");
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        producer.start();
        String key = UUID.randomUUID().toString();
        Message message = new Message("keyTopic", "vip1", key, "我是vip1".getBytes());
        producer.send(message);
        System.out.println("发送成功");
        producer.shutdown();
    }


    @Test
    public void keyConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("key_consumer_group-a");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("keyTopic", "vip1");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                System.out.println("我是VIP1消费者，我正在消费" + new String(messageExt.getBody()));
                System.out.println("我们业务的标识" + messageExt.getKeys());
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
}
