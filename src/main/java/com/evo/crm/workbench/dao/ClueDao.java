package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.Clue;

public interface ClueDao {


    Integer addClue(Clue clue);

    Clue selectClueById(String id);

    Clue getClueById(String id);

    Integer deleteClue(String clueId);
}
