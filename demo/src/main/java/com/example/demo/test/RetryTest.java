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

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RetryTest {


    @Test
    public void retryProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("retry_producer_group");
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        producer.start();

        //生产者发送消息 重试次数
        producer.setRetryTimesWhenSendFailed(2);
        producer.setRetryTimesWhenSendAsyncFailed(2);

        String key = UUID.randomUUID().toString();
        System.out.println("key:" + key);
        Message message = new Message("retryTopic", "vip1", key, "我是vip1".getBytes());
        producer.send(message);
        System.out.println("发送成功");
        producer.shutdown();
    }


    /*
    默认重试16次
    一直重试失败会放到死信消息队列中  %DQL%
     */
    @Test
    public void retryConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry_consumer_group-a");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("retryTopic", "*");

        //设定重试次数
        consumer.setMaxReconsumeTimes(2);

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                System.out.println(new Date());
                System.out.println("重试次数:" + messageExt.getReconsumeTimes());
                System.out.println(new String(messageExt.getBody()));
                //业务报错 null 或者 reconsume都会重试
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
        System.in.read();
    }


    @Test
    public void retryDeadConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry_consumer_group-a");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("%DLQ%retry_consumer_group-a", "*");

        //设定重试次数
        consumer.setMaxReconsumeTimes(2);

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                System.out.println(new Date());
                System.out.println("通知人工等方式，记录到特定的位置");
                System.out.println(new String(messageExt.getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }



    @Test
    public void retryConsumer2() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("retry_consumer_group-a");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("retryTopic", "*");

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                System.out.println(new Date());
                //业务处理代码块
                try {
                    //做业务逻辑
                    handleDb();
                    int i = 10 / 0;
                }catch (Exception e){
                    int reconsumeTimes = messageExt.getReconsumeTimes();
                    if (reconsumeTimes >= 3){
                        System.out.println("通知人工等方式，记录到特定的位置");
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                System.out.println("重试次数:" + messageExt.getReconsumeTimes());
                System.out.println(new String(messageExt.getBody()));
                //业务报错 null 或者 reconsume都会重试
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.in.read();
    }

    private void handleDb() {
    }
}
