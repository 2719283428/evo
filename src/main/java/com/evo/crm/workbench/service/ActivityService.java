package com.evo.crm.workbench.service;


import com.evo.crm.vo.PaginationVO;
import com.evo.crm.workbench.domain.Activity;
import com.evo.crm.workbench.domain.ActivityRemark;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ActivityService {
    String addActivity(Activity activity,HttpServletRequest request);

    PaginationVO<Activity> getActivityPageVO(Integer pageNo, Integer pageSize, Activity activity);

    boolean deleteActivity(String[] ids);

    Map<String,Object> getUserAndActivity(String id);

    String editActivity(Activity activity, HttpServletRequest request);

    Activity queryActivityById(String id);

    List<ActivityRemark> getActivityRemarkById(String id);

    Integer removeActivityRemark(String id);

    Integer addActivityRemark(ActivityRemark activityRemark, HttpServletRequest request);

    Integer editActivityRemark(ActivityRemark activityRemark, HttpServletRequest request);

    List<Activity> getActivityByClueId(String clueId);

    List<Activity> getActivityListByNameNotInClueId(Map<String, String> map);

    List<Activity> getActivityListByName(String aname);
}
