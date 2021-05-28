package com.evo.crm.utils;

import com.evo.crm.workbench.domain.DicValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.*;


@Component
public class RedisUtil {
    @Autowired
    Jedis jedis;

    public void ping(){
        System.out.println(jedis.ping());
    }

    //将list集合根据数据库编号放入到redis
    public void addMap(String code,List<DicValue> dicValueList) {
        int num = 1;
        for(DicValue dicValue : dicValueList){
            Class dicValueClass = DicValue.class;


            //用反射机制获取所有属性
            Field[] fields = dicValueClass.getDeclaredFields();
            for(Field field : fields){
                //打破封装
                field.setAccessible(true);
                //获取属性名和属性值
                String fieldName = field.getName();
                String fieldValue = null;
                try {
                    //获取属性值
                    fieldValue = (String) field.get(dicValue);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                jedis.hset(code+num,fieldName,fieldValue);
                jedis.sadd(code,code+num);

            }
            num++;
        }
    }

    //根据DicValue中typeCode查询Redis中的DicValue  返回一个
    public List<DicValue> getDicValueByCode(String code) {
        //获取所有存放所有数据库key的Set
        Set<String> numSet = jedis.smembers(code);
        Iterator itNum = numSet.iterator();

        //创建返回的List集合
        List<DicValue> dicValueList = new ArrayList();
        for(int i=0;i < numSet.size();i++){
            dicValueList.add(i,null);
        }
        //遍历数据库key的Set
        while (itNum.hasNext()){


            //根据Set中的数据库key查询
            //获取DicValue对象以String形式存储对象
            String numName = (String) itNum.next();
            Map<String,String> dicValueMap = jedis.hgetAll(numName);
            //将map集合转换为set方便遍历
            Set<String> mapSet =  dicValueMap.keySet();

            //获取迭代器，遍历set集合
            Iterator it= mapSet.iterator();
            //利用反射机制创建并设置其中属性，以便于返回
            Class dicValueClass = DicValue.class;
            DicValue newDicValue = null;



            try {
                //反射机制创建对象
                newDicValue = (DicValue) dicValueClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            //set集合长度和DicValue中的属性属性数量一致
            while (it.hasNext()){
                //获取属性名
                String name = (String) it.next();
                //获取属性值
                String value = dicValueMap.get(name);
                Field field = null;
                try {
                    //获取要操作的属性对象
                    field = dicValueClass.getDeclaredField(name);
                    //打破封装
                    field.setAccessible(true);
                    //将获取到的属性值设置到newDicValue对象中
                    field.set(newDicValue,value);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            //将封装好的DicValue根据orderNo按顺序放入到List集合
            int index = Integer.valueOf(newDicValue.getOrderNo()) - 1;
            dicValueList.set(index,newDicValue);

        }

        return dicValueList;
    }

    /**
     * @param all 为true 表示 只清除当前数据库
     * @param all 为false 表示 清除所有数据库
     */

    public void flush(boolean all) {
        if(all){
            jedis.flushDB();
        }else {
            jedis.flushAll();
        }
    }
}
