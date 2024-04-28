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

import java.util.List;

public class SimpleTest {

    @Test
    public void testMehod() {
        System.out.println("asdfasdfsdf");
    }


    @Test
    public void testSimpleProducer() throws Exception{
        //创建一个生产者 并 指定一个组名
        DefaultMQProducer producer = new DefaultMQProducer("test-producer-group");
        //连接nameserver
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        //启动
        producer.start();
        //创建一个消息
        Message message = new Message("test-topic", "简单尝试".getBytes());
        SendResult sendResult = producer.send(message);
        System.out.println(sendResult.getSendStatus());
        //关闭生产者
        producer.shutdown();
    }
    @Test
    public void testSimpleConsumer() throws Exception {
        //创建一个消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-consumer-group");
        //连接nameserver
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        //订阅一个主题 * 代表所有消息
        consumer.subscribe("test-topic", "*");
        //设置一个监听器 (一直监听、异步回调）
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                //正常业务逻辑区域
                System.out.println("我是消费者");
                System.out.println("消息内容:" + new String(msgs.get(0).getBody()));
                System.out.println("消费上下文:" + context.toString());

                //返回值 SUCCESS代表消息从MQ消费了，出队
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动
        consumer.start();
        //挂起当前JVM
        System.in.read();
    }
}
