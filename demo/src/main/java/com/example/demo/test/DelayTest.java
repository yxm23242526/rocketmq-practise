package com.example.demo.test;

import com.example.demo.consts.MQConsts;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class DelayTest {

    @Test
    public void delayProducerTest() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("delay_producer_group");
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        producer.start();
        Message message = new Message("orderDelayTopic", "订单号，座位号".getBytes());

        //给消息设置一个延迟时间
        message.setDelayTimeLevel(3);
        //发延迟消息
        SendResult sendResult = producer.send(message);
        System.out.println("发送时间" + new Date());
        producer.shutdown();
    }

    @Test
    public void delayConsumerTest() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("delay_consumer_group");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("orderDelayTopic", "*");
        consumer.registerMessageListener(
                new MessageListenerConcurrently() {
                    @Override
                    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                        System.out.println("收到消息" + new Date());
                        System.out.println(new String(msgs.get(0).getBody()));
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                }
        );
        consumer.start();
        System.in.read();
    }
}
