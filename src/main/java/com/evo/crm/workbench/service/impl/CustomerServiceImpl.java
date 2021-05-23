package com.evo.crm.workbench.service.impl;

import com.evo.crm.workbench.dao.CustomerDao;
import com.evo.crm.workbench.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDao customerDao;


    //取得客户名称列表（模糊查询）
    @Override
    public List<String> getCustomerName(String name) {
        List<String> list = customerDao.selectCustomerByBlurryName(name);
        return list;
    }
}
