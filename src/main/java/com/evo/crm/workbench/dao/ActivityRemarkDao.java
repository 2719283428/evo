package com.evo.crm.workbench.dao;

import com.evo.crm.workbench.domain.ActivityRemark;

import java.util.List;

public interface ActivityRemarkDao {
    Integer selectActivityRemarkByAids(String[] ids);

    Integer deleteActivityRemarkByAids(String[] ids);

    List<ActivityRemark> selectActivityRemarkByAid(String id);

    Integer deleteActivityRemark(String id);

    Integer insertActivityRemark(ActivityRemark activityRemark);

    Integer updateActivityRemark(ActivityRemark activityRemark);
}
