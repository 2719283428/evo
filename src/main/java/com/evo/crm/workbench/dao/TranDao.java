package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    Integer insertTran(Tran tran);

    Tran selectById(String id);

    Integer updateTran(Tran tran);

    Integer selectStageCount();

    List<Map<String, Object>> selectStageGroupCount();
}
