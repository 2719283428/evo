package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer selectCustomerByName(String name);

    Integer insertCustomer(Customer customer);

    List<String> selectCustomerByBlurryName(String name);
}
