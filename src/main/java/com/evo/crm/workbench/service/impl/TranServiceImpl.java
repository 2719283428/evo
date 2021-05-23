package com.evo.crm.workbench.service.impl;

import com.evo.crm.settings.dao.UserDao;
import com.evo.crm.settings.domain.User;
import com.evo.crm.utils.DateTimeUtil;
import com.evo.crm.utils.RedisUtil;
import com.evo.crm.utils.UUIDUtil;
import com.evo.crm.workbench.dao.CustomerDao;
import com.evo.crm.workbench.dao.TranDao;
import com.evo.crm.workbench.dao.TranHistoryDao;
import com.evo.crm.workbench.domain.Customer;
import com.evo.crm.workbench.domain.DicValue;
import com.evo.crm.workbench.domain.Tran;
import com.evo.crm.workbench.domain.TranHistory;
import com.evo.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TranServiceImpl implements TranService {
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserDao userDao;
    @Autowired
    private CustomerDao customerDao;

    //获取用户，数据字典中的数据
    @Override
    public Map<String, List> display(Map<String,String> typeCodesMap) {


        Map<String,List> map = new HashMap();

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

        List<User> userList = userDao.selectUserAll();
        map.put("userList",userList);


        return map;
    }


    //添加交易和交易历史(如果没有客户，添加客户)
    @Override
    public Boolean addTran(Tran tran,String customerName) {
        //返回标记
        boolean flag = true;

        //查询是否已存在客户
        Customer customer = customerDao.selectCustomerByName(customerName);
        //如果customer等于null说明没有客户，则新建一个客户
        if(null == customer){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setOwner(tran.getOwner());

            //调用dao添加客户
            Integer insertResult = customerDao.insertCustomer(customer);
            if(insertResult != 1){
                flag = false;
            }
        }


        //将客户id封装到tran对象中
        tran.setCustomerId(customer.getId());
        //添加交易
        Integer TranResult = tranDao.insertTran(tran);
        if(TranResult != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());

        Integer resultHistory =  tranHistoryDao.insertTranHistory(tranHistory);
        if(resultHistory != 1){
            flag = false;
        }



        return flag;
    }


    //跳转到详细信息页,根据id查交易记录
    @Override
    public Tran queryById(String id) {
        Tran tran = tranDao.selectById(id);
        return tran;
    }


    //根据交易表id查询交易历史表
    @Override
    public List<TranHistory> queryHistoryListByTranId(String tranId) {
        List<TranHistory> tranHistoryList = tranHistoryDao.selectByTranId(tranId);
        return tranHistoryList;
    }



    //改变交易阶段，新增交易历史
    @Override
    public Boolean changeStage(Tran tran) {
        boolean flag = true;
        Integer result = tranDao.updateTran(tran);
        if(result != 1){
            flag = false;
        }


        //交易阶段改变后，生成一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setStage(tran.getStage());
        th.setCreateBy(tran.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(tran.getExpectedDate());
        th.setMoney(tran.getMoney());
        th.setTranId(tran.getId());

        //添加交易历史
        Integer resultHistory = tranHistoryDao.insertTranHistory(th);
        if(resultHistory != 1){
            flag = false;
        }



        return flag;
    }



    //给交易阶段数量统计漏斗图提供数据
    @Override
    public Map<String, Object> getCharts() {
        Map<String, Object> map = new HashMap();

        //取得stage记录总条数（total）
        Integer total = tranDao.selectStageCount();

        //取得dataList
        List<Map<String,Object>> dataList = tranDao.selectStageGroupCount();

        map.put("total",total);
        map.put("dataList",dataList);

        return map;
    }
}
