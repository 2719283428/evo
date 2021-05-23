package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> selectTypeCode(String code);

    String selectAll();

    DicValue selectById(DicValue dicValue);
}
