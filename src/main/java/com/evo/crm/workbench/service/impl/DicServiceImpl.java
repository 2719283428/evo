package com.evo.crm.workbench.service.impl;

import com.evo.crm.settings.domain.User;
import com.evo.crm.utils.DateTimeUtil;
import com.evo.crm.utils.UUIDUtil;
import com.evo.crm.workbench.dao.ClueDao;
import com.evo.crm.workbench.dao.DicTypeDao;
import com.evo.crm.workbench.dao.DicValueDao;
import com.evo.crm.workbench.domain.Clue;
import com.evo.crm.workbench.domain.DicType;
import com.evo.crm.workbench.domain.DicValue;
import com.evo.crm.workbench.service.DicService;
import com.evo.crm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Service
public class DicServiceImpl implements DicService {
    @Autowired
    private DicTypeDao dicTypeDao;
    @Autowired
    private DicValueDao dicValueDao;
    @Autowired
    private ClueDao clueDao;
    @Autowired
    private RedisUtil redisUtil;

    //将数据字典中的数据按类型查询，放入到Redis中
    @Override
    public void setDictionaryRedis() {
        //查询出所有数据字典类型
        List<DicType> dicTypeList = dicTypeDao.selectAll();

        for(DicType dicType:dicTypeList){
            //根据DicType的code属性查询DicValue
            List<DicValue> dicValueList = dicValueDao.selectTypeCode(dicType.getCode());
            //调用redis工具类的方法
            redisUtil.addMap(dicType.getCode(),dicValueList);

        }
    }

    //根据DicValue的typeCode查询
    @Override
    public Map<String,List<DicValue>> getDicValueSet(Map<String,String> typeCodesMap) {
        Map<String,List<DicValue>> map = new HashMap();

        //将map集合转为set集合并获取迭代器
        Set<String> set = typeCodesMap.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()){
            String key = it.next();

            //调用redis工具类根据每个typeCode查询DicValue
            List<DicValue> dicValueList = redisUtil.getDicValueByCode(typeCodesMap.get(key));
            //放入map集合
            map.put(key,dicValueList);
        }


        return map;
    }

    //添加Clue
    @Override
    public Integer saveClue(Clue clue, HttpServletRequest request) {
        //设置id
        clue.setId(UUIDUtil.getUUID());
        //设置创建人
        clue.setCreateBy(((User)request.getSession().getAttribute("user")).getName());
        //设置创建时间
        clue.setCreateTime(DateTimeUtil.getSysTime());

        Integer result = clueDao.addClue(clue);
        return result;
    }

    //根据id查询Clue
    @Override
    public Clue detailClue(String id) {
        Clue clue = clueDao.selectClueById(id);
        return clue;
    }
}
