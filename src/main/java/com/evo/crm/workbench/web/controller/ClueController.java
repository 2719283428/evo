package com.evo.crm.workbench.web.controller;

import com.evo.crm.settings.domain.User;
import com.evo.crm.workbench.domain.Activity;
import com.evo.crm.workbench.domain.Clue;
import com.evo.crm.workbench.domain.Tran;
import com.evo.crm.workbench.service.ActivityService;
import com.evo.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ClueController {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ClueService clueService;


    //查询未的关联市场活动
    @RequestMapping(value = "/workbench/clue/getActivityList.do")
    public @ResponseBody List<Activity> associate(String aname, String clueId){
        Map<String,String> map = new HashMap();
        map.put("aname",aname);
        map.put("clueId",clueId);
        List<Activity> activityList = activityService.getActivityListByNameNotInClueId(map);

        return activityList;
    }


    //关联市场活动
    @RequestMapping(value = "/workbench/clue/bund.do")
    public @ResponseBody Integer bund(String clueId, String[] activityId){
        Integer result = clueService.bundActivityAndClue(clueId,activityId);
        return result;
    }


    //查询未的关联市场活动
    @RequestMapping(value = "/workbench/clue/getActivityListByName.do")
    public @ResponseBody List<Activity> ActivityListByName(String aname){
        List<Activity> activityList = activityService.getActivityListByName(aname);

        return activityList;
    }


    //转换未创建交易的客户
    @RequestMapping(value = "/workbench/clue/convert.do")
    public ModelAndView convert(String clueId, Integer flag, Tran tran, HttpServletRequest request){

        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        boolean flagConvert = clueService.convert(clueId,tran,createBy,flag);
        //使用重定向转发
        ModelAndView mv = new ModelAndView();
        mv.setViewName("forward:/workbench/clue/index.jsp");
        return mv;
    }


}
