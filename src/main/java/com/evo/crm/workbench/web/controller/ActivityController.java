package com.evo.crm.workbench.web.controller;

import com.evo.crm.settings.domain.User;
import com.evo.crm.settings.service.UserService;
import com.evo.crm.vo.PaginationVO;
import com.evo.crm.workbench.domain.Activity;
import com.evo.crm.workbench.domain.ActivityRemark;
import com.evo.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;

    //获取所有用户
    @RequestMapping(value = "/workbench/activity/getUserList.do")
    public @ResponseBody Object activityUserAll(){
        List<User> userList = userService.getUserList();
        return userList;
    }

    //创建市场活动
    @RequestMapping(value = "/workbench/activity/save.do")
    public @ResponseBody String activitySave(Activity activity, HttpServletRequest request){
        String result = activityService.addActivity(activity,request);
        return result;
    }

    //分页查询市场活动列表
    @RequestMapping(value = "/workbench/activity/pageList.do")
    public @ResponseBody Object activityPageList(Integer pageNo,Integer pageSize,Activity activity){
        PaginationVO<Activity> vo = activityService.getActivityPageVO(pageNo,pageSize,activity);
        //使用VO做返回
        return vo;
    }

    //删除市场活动
    @RequestMapping(value = "/workbench/activity/delete.do")
    public @ResponseBody Object activityDelete(String[] id){
        boolean flag = activityService.deleteActivity(id);
        Map<String,Boolean> map = new HashMap<>();
        map.put("success",flag);
        return map;
    }

    //获取修改市场模块窗口相关数据
    @RequestMapping(value = "/workbench/activity/userAndActivity.do")
    public @ResponseBody Object getUserAndActivity(String id){
        Map<String,Object> map = activityService.getUserAndActivity(id);
        return map;
    }

    //修改市场模块
    @RequestMapping(value = "/workbench/activity/update.do")
    public @ResponseBody String activityUpdate(Activity activity, HttpServletRequest request){
        String result = activityService.editActivity(activity,request);
        return result;
    }


    //跳转到详细信息页
    @RequestMapping(value = "/workbench/activity/detail.do")
    public String activityDetail(String id,HttpServletRequest request){
        Activity activity = activityService.queryActivityById(id);
        request.getSession().setAttribute("activity",activity);

        return "/workbench/activity/detail.jsp";
    }


    //返回给前端市场活动备注
    @RequestMapping(value = "/workbench/activity/remark.do")
    public @ResponseBody Object activityRemark(String activityId){
        List<ActivityRemark> activityRemarkList = activityService.getActivityRemarkById(activityId);
        return activityRemarkList;
    }

    //删除市场活动
    @RequestMapping(value = "/workbench/activity/deleteRemark.do")
    public @ResponseBody Integer activityRemarkDelete(String id){
        Integer result = activityService.removeActivityRemark(id);
        return result;
    }


    //添加市场活动备注
    @RequestMapping(value = "/workbench/activity/addRemark.do")
    public @ResponseBody Integer activityRemarkAdd(ActivityRemark activityRemark, HttpServletRequest request){
        Integer result = activityService.addActivityRemark(activityRemark,request);
        return result;
    }

    //添加市场活动备注
    @RequestMapping(value = "/workbench/activity/updateRemark.do")
    public @ResponseBody Integer activityRemarkUpdate(ActivityRemark activityRemark, HttpServletRequest request){
        Integer result = activityService.editActivityRemark(activityRemark,request);
        return result;
    }



}
