package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.ClueRemark;

import java.util.List;

public interface ClueRemarkDao {

    List<ClueRemark> selectByClueId(String clueId);

    Integer delete(ClueRemark clueRemark);
}
