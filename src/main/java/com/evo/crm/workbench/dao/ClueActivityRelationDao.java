package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    Integer deleteActivityAndClue(String clueId);

    Integer insertAll(ClueActivityRelation clueActivityRelation);

    List<ClueActivityRelation> selectListByClueId(String clueId);
}
