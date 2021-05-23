package com.evo.crm;

import com.evo.crm.utils.RedisUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.Map;


public class MyTest {
    @Test
    public void test1(){
        Jedis jedis = new Jedis("192.168.28.130",6379);
        System.out.println(jedis.ping());
    }

    @Test
    public void test2(){
        Jedis jedis = new Jedis("192.168.28.130",6379);
        Map<String, String> s = jedis.hgetAll("clueState7");
        System.out.println(s.get("orderNo"));
    }

    @Test
    public void test3(){
        RedisUtil redisUtil = new RedisUtil();
        //DicValue dicValue = redisUtil.getDicValueByCode("clueState7");
    }

}
