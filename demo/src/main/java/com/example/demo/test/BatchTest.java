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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BatchTest {


    @Test
    public void testBatchProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("batch_producer_group");
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        producer.start();
        //放到一个queue中
        List<Message> msgs = Arrays.asList(
                new Message("batchTopic", "batchA".getBytes()),
                new Message("batchTopic", "batchB".getBytes()),
                new Message("batchTopic", "batchC".getBytes())
        );
        //发延迟消息
        SendResult sendResult = producer.send(msgs);
        System.out.println(sendResult);
        producer.shutdown();
    }

    @Test
    public void testBatchConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("batch_consumer_group");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("batchTopic", "*");
        //放到一个queue中
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
