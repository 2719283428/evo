package com.evo.crm.settings.service.impl;

import com.evo.crm.settings.dao.UserDao;
import com.evo.crm.settings.domain.User;
import com.evo.crm.settings.service.UserService;
import com.evo.crm.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public Map<String,Object> login(String loginAct, String loginPwd, String ip) {
        //resultMap 要返回给Controller的map
        Map<String,Object> resultMap = new HashMap();
        //logMap 给dao层的实参
        Map<String,String> logMap = new HashMap<>();
        logMap.put("loginAct",loginAct);
        logMap.put("loginPwd",loginPwd);
        //获得查询结果
        User user = userDao.login(logMap);


        resultMap.put("success",false);
        //验证账号密码
        if(null == user){
            resultMap.put("loginMsg","账号密码错误");
            return resultMap;
        }
        //验证失效时间
        if( (user.getExpireTime().compareTo(DateTimeUtil.getSysTime())) < 0){
            resultMap.put("loginMsg","账号已失效");
            return resultMap;
        }
        //判断锁定状态
        if("0".equals(user.getLockState())){
            resultMap.put("loginMsg","账号已锁定");
            return resultMap;
        }
        //判断IP地址
        if(!user.getAllowIps().contains(ip)){
            resultMap.put("loginMsg","ip地址受到限制");
            return resultMap;
        }


        //正常登录
        resultMap.put("success",true);
        resultMap.put("user",user);
        return resultMap;
    }

    @Override
    public List<User> getUserList() {
        List<User> userList = userDao.selectUserAll();
        return userList;
    }
}
