package com.example.controller;


import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeckillController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 1. 用户去重
     * 2. 库存是否充足
     * 3. 消息放入mq
     * @param goodsId
     * @param userId
     * @return
     */
    @GetMapping("seckill")
    public String doSeckill(Integer goodsId, Integer userId) {
        //登陆之后这个userId可以从token中取，没必要传参数

        //需要一个Uniquekey 最好是再加上一个ttl，保证每天能抢
        String key = "seckill:"+ userId + "-" + goodsId;
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "");
        if (Boolean.FALSE.equals(flag)){
            return "已经抢过秒杀了";
        }
        Long count = stringRedisTemplate.opsForValue().decrement("goodsId:" + goodsId);
        if (count != null && count < 0){
            return "该商品已经被抢完了";
        }
        //放入MQ中，异步处理
        rocketMQTemplate.asyncSend("seckillTopic", key, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("发送成功");
            }

            @Override
            public void onException(Throwable e) {
                System.out.println("发送失败:" + e.getMessage());
            }
        });
        return "正在拼命抢购中,请稍后在订单中心查看";
        //线程不安全的写法
       /* String s = stringRedisTemplate.opsForValue().get(goodsId);
        int count = Integer.parseInt(s);
        count--;
        if (count < 0){
            return "该商品已经被抢完了";
        }
        stringRedisTemplate.opsForValue().set(String.valueOf(goodsId), String.valueOf(count));*/
    }
}
