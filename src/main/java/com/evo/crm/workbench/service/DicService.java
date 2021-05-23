package com.evo.crm.workbench.service;

import com.evo.crm.workbench.domain.Clue;
import com.evo.crm.workbench.domain.DicValue;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface DicService {
    void setDictionaryRedis();

    Map<String,List<DicValue>> getDicValueSet(Map<String,String> typeCodesMap);

    Integer saveClue(Clue clue, HttpServletRequest request);

    Clue detailClue(String id);
}
