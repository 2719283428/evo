package com.evo.crm.workbench.service;

import com.evo.crm.workbench.domain.DicValue;
import com.evo.crm.workbench.domain.Tran;
import com.evo.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    Map<String, List> display(Map<String,String> map);

    Boolean addTran(Tran tran,String customerName);

    Tran queryById(String id);

    List<TranHistory> queryHistoryListByTranId(String tranId);

    Boolean changeStage(Tran tran);

    Map<String, Object> getCharts();
}
