package com.example.demo;

import com.example.demo.consts.MQConsts;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    void repeatProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer("repeat-producer-group");
        producer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        producer.start();
        String key = UUID.randomUUID().toString();
        System.out.println(key);
        Message m1 = new Message("repeatTopic", null, key, "扣减库存-1".getBytes());
        Message m1repeat = new Message("repeatTopic", null, key, "扣减库存-1".getBytes());
        producer.send(m1);
        producer.send(m1repeat);
        System.out.println("发送成功");
        producer.shutdown();
    }


    /*
    设计一个去重表，对消息的唯一key添加唯一索引
    每次消费消息的时候，先插入数据库，如果成功则这行业务逻辑
    （数据库唯一键值是不允许重复插入的，所以可以先插入，抓SQL的异常）
    如果插入失败，则说明消息来过了
     */
    @Test
    public void repeatConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("repeat_consumer_group");
        consumer.setNamesrvAddr(MQConsts.NAME_SRV_ADDR);
        consumer.subscribe("repeatTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = msgs.get(0);
                String keys = messageExt.getKeys();
                try{
                    jdbcTemplate.update("insert into order_oper_log('type','order_sn','user') values (1, ?, '123')", keys);
                }catch (Exception e){

                }
                System.out.println(new String(messageExt.getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

            }
        });
        consumer.start();
        System.in.read();
    }
}
