package com.evo.crm.workbench.service;

import com.evo.crm.workbench.domain.Tran;

import javax.servlet.http.HttpServletRequest;

public interface ClueService {
    Integer unbundActivityAndClue(String clueId);

    Integer bundActivityAndClue(String clueId, String[] activityId);

    boolean convert(String clueId, Tran tran, String createBy,Integer flagCon);
}
