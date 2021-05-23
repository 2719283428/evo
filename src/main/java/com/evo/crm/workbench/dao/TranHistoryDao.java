package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    Integer insertTranHistory(TranHistory tranHistory);

    List<TranHistory> selectByTranId(String tranId);
}
