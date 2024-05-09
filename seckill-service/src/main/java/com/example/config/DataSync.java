package com.example.config;

import com.example.domain.Goods;
import com.example.mapper.GoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DataSync {


    //一般认为通过定时任务将mysql的库存同步到redis
    /*@Scheduled(cron = "0 0 10 0 0 ?")
    public void initData(){

    }*/

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //这里希望项目启动的时候就同步 但是需要再类的属性注入完毕以后
    @PostConstruct
    public void initData(){
        List<Goods> goodsList = goodsMapper.selectSeckillGoods();
        if (CollectionUtils.isEmpty(goodsList)){
            return;
        }
        goodsList.forEach(goods -> {
            stringRedisTemplate.opsForValue().set("goodsId:" + goods.getId(), goods.getStocks().toString());
        });
    }
}
