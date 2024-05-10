package com.example.listen;


import com.example.service.GoodsService;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RocketMQMessageListener(topic = "seckillTopic",
        consumerGroup = "seckill-consumer-group",
        consumeMode = ConsumeMode.CONCURRENTLY,
        consumeThreadNumber = 40)
public class SeckillListener implements RocketMQListener<MessageExt> {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    int selfRotate_Time = 10000;
    // @Override
    // public void onMessage(MessageExt message) {
    //     String msg = new String(message.getBody());
    //     Integer userId = Integer.parseInt(msg.split("-")[0]);
    //     Integer goodsId = Integer.parseInt(msg.split("-")[1]);
    //     //这样能保证事务在锁内提交， 但不能解决JVM的分布式问题
    //     synchronized (this){
    //         goodsService.realSeckill(/*userId, */goodsId);
    //     }
    // }

    public void onMessage(MessageExt message) {
        String msg = new String(message.getBody());
        Integer userId = Integer.parseInt(msg.split("-")[0]);
        Integer goodsId = Integer.parseInt(msg.split("-")[1]);

        //锁自旋，其实就是不停等待直到获取，不然直接Break了
        int currentThreadTime = 0;
        while (currentThreadTime < selfRotate_Time){
            String key = "lock:" + goodsId;
            //过期时间是防止业务逻辑卡死走不到finally导致锁永远不释放
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "", Duration.ofSeconds(30));
            if (Boolean.FALSE.equals(flag)){
                //拿到锁成功
                try {
                    goodsService.realSeckill2(userId, goodsId);
                    return;
                } finally {
                    //删除
                    stringRedisTemplate.delete(key);
                }
            }else {
                currentThreadTime += 200;
                //这里算是一个优化，因为拿不到的情况下 不需要立刻获取，所以可以先休息200ms
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
