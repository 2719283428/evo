package com.evo.crm.workbench.service.impl;

import com.evo.crm.settings.dao.UserDao;
import com.evo.crm.settings.domain.User;
import com.evo.crm.utils.DateTimeUtil;
import com.evo.crm.utils.UUIDUtil;
import com.evo.crm.vo.PaginationVO;
import com.evo.crm.workbench.dao.ActivityDao;
import com.evo.crm.workbench.dao.ActivityRemarkDao;
import com.evo.crm.workbench.domain.Activity;
import com.evo.crm.workbench.domain.ActivityRemark;
import com.evo.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivityRemarkDao activityRemarkDao;
    @Autowired
    private UserDao userDao;

    //新建市场活动
    @Override
    public String addActivity(Activity activity,HttpServletRequest request) {
        activity.setId(UUIDUtil.getUUID());
        //创建时间
        String createTime = DateTimeUtil.getSysTime();
        activity.setCreateTime(createTime);

        //获取当前用户
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        activity.setCreateBy(createBy);

        //调用dao新添活动列表致数据库
        String result = String.valueOf(activityDao.insertActivity(activity));
        return result;
    }


    //分页查询市场活动
    @Override
    public PaginationVO<Activity> getActivityPageVO(Integer pageNo, Integer pageSize, Activity activity) {
        //获取条件查询结果总记录条数
        List<Activity> totalList = activityDao.selectActivityAll(activity);
        Integer total = totalList.size();

        //使用分页工具执行分页查询
        PageHelper.startPage(pageNo,pageSize);
        List<Activity> activityList = activityDao.selectActivityAll(activity);

        //将数据封装到VO中
        PaginationVO<Activity> vo = new PaginationVO();
        vo.setDataList(activityList);
        vo.setTotal(total);
        return vo;
    }


    //删除市场活动
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT)
    @Override
    public boolean deleteActivity(String[] ids) {
        boolean flag = true;
        //查询出需要删除的备注数量
        int count = activityRemarkDao.selectActivityRemarkByAids(ids);
        //删除备注，返回受到影响的条数（实际删除的条数）
        int result = activityRemarkDao.deleteActivityRemarkByAids(ids);
        if(count != result){
            flag = false;
        }
        //删除市场活动
        int aResult = activityDao.deleteActivityById(ids);
        if(aResult!=ids.length){
            flag = false;
        }


        return flag;
    }


    //获取修改市场模块窗口相关数据返回一个map
    @Override
    public Map<String,Object> getUserAndActivity(String id) {
        Map<String,Object> map = new HashMap<>();

        //获取所有用户列表
        List<User> userList = userDao.selectUserAll();
        map.put("userList",userList);

        //按id查询市场活动列表
        Activity activity = activityDao.selectActivityById(id);


        map.put("activity",activity);
        return map;
    }


    //修改活动列表
    @Override
    public String editActivity(Activity activity, HttpServletRequest request) {

        //获取当前登录用户
        User user = (User) request.getSession().getAttribute("user");
        //设置修改人
        activity.setEditBy(user.getName());

        //获取修改时间
        String createTime = DateTimeUtil.getSysTime();
        activity.setEditTime(createTime);

        //调用dao获取修改的条数  1 或 0
        String result = String.valueOf(activityDao.updateActivity(activity));
        return result;
    }

    //根据id查询activity
    @Override
    public Activity queryActivityById(String id) {
        Activity activity = activityDao.selectActivityDetailsById(id);
        return activity;
    }


    //根据id获得ActivityRemark
    @Override
    public List<ActivityRemark> getActivityRemarkById(String id) {
        List<ActivityRemark> activityRemarkList = activityRemarkDao.selectActivityRemarkByAid(id);
        return activityRemarkList;
    }

    //删除市场活动备注
    @Override
    public Integer removeActivityRemark(String id) {
        //调用dao根据id删除市场活动备注
        Integer result = activityRemarkDao.deleteActivityRemark(id);
        return result;
    }

    //添加市场活动备注
    @Override
    public Integer addActivityRemark(ActivityRemark activityRemark, HttpServletRequest request) {
        //设置id
        activityRemark.setId(UUIDUtil.getUUID());
        //设置创建时间
        activityRemark.setCreateTime(DateTimeUtil.getSysTime());
        //设置创建人
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        activityRemark.setCreateBy(createBy);

        Integer result = activityRemarkDao.insertActivityRemark(activityRemark);
        return result;
    }

    //修改市场活动备注
    @Override
    public Integer editActivityRemark(ActivityRemark activityRemark, HttpServletRequest request) {
        //设置修改人
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        activityRemark.setEditBy(editBy);
        //设置修改时间
        activityRemark.setEditTime(DateTimeUtil.getSysTime());

        Integer result = activityRemarkDao.updateActivityRemark(activityRemark);
        return result;
    }


    //获取关联的市场活动信息列表
    @Override
    public List<Activity> getActivityByClueId(String clueId) {
        List<Activity> activityList = activityDao.selectActivityByClueId(clueId);
        return activityList;
    }



    //根据name模糊查询查询市场活动列表（不包括已关联的市场活动）
    @Override
    public List<Activity> getActivityListByNameNotInClueId(Map<String, String> map) {
        List<Activity> activityList = activityDao.selectActivityAndClueId(map);
        return activityList;
    }


    //根据name模糊查询查询市场活动列表（不包括已关联的市场活动）
    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> activityList = activityDao.selectActivityByName(aname);
        return activityList;
    }
}



