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

public class TagTest {



    @Test
    public void tagProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("tag_producer_group");
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        producer.start();
        Message message = new Message("tagTopic", "vip1", "我是vip1".getBytes());
        Message message2 = new Message("tagTopic", "vip2", "我是vip2".getBytes());
        producer.send(message);
        producer.send(message2);
        System.out.println("发送成功");
        producer.shutdown();
    }


    /*
    vip1
     */
    @Test
    public void tagConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag_consumer_group-a");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("tagTopic", "vip1");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("我是VIP1消费者，我正在消费" + new String(msgs.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }

    /*
    vip1 || vip2
     */
    @Test
    public void tagConsumer2() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("tag_consumer_group-b");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("tagTopic", "vip1 || vip2");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("我是VIP2消费者，我正在消费" + new String(msgs.get(0).getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }
}
