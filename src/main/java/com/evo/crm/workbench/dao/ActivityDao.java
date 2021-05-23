package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    Integer insertActivity(Activity activity);

    List<Activity> selectActivityAll(Activity activity);

    Integer deleteActivityById(String[] ids);

    Activity selectActivityById(String id);

    Integer updateActivity(Activity activity);

    Activity selectActivityDetailsById(String id);

    List<Activity> selectActivityByClueId(String clueId);

    List<Activity> selectActivityAndClueId(Map<String, String> map);

    List<Activity> selectActivityByName(String aname);
}
