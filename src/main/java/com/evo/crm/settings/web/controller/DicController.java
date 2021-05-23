package com.evo.crm.settings.web.controller;

import com.evo.crm.settings.domain.User;
import com.evo.crm.workbench.domain.Activity;
import com.evo.crm.workbench.domain.Clue;
import com.evo.crm.workbench.domain.DicValue;
import com.evo.crm.workbench.service.ActivityService;
import com.evo.crm.workbench.service.ClueService;
import com.evo.crm.workbench.service.DicService;
import com.evo.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DicController {
    @Autowired
    private DicService dicService;
    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ClueService clueService;

    //获取用户列表
    @RequestMapping(value = "/workbench/clue/userList.do")
    public @ResponseBody Object userList(){
        List<User> userList = userService.getUserList();
        return userList;
    }

    //获取数据字典
    @RequestMapping(value = "/workbench/clue/dicValueCreateList.do")
    public @ResponseBody Object dicValueCreateList(String createAppellation,String createStatus,String createSource){
        Map<String,String> map = new HashMap();
        map.put("appellation",createAppellation);
        map.put("clueState",createStatus);
        map.put("source",createSource);
        Map<String,List<DicValue>> retMap = dicService.getDicValueSet(map);
        return retMap;
    }


    //保存Clue
    @RequestMapping(value = "/workbench/clue/save.do")
    public @ResponseBody Integer save(Clue clue, HttpServletRequest request){
        Integer result = dicService.saveClue(clue,request);
        return result;
    }


    //跳转到详细信息页
    @RequestMapping(value = "/workbench/clue/detail.do")
    public String detail(String id,HttpServletRequest request){
        Clue clue = dicService.detailClue(id);
        request.getSession().setAttribute("clue",clue);
        return "/workbench/clue/detail.jsp";
    }


    //取出关联的市场活动信息列表
    @RequestMapping(value = "/workbench/clue/ActivityByClueId.do")
    public @ResponseBody Object ActivityByClueId(String clueId){
        List<Activity> activityList = activityService.getActivityByClueId(clueId);

        return activityList;

    }


    //解除关联的市场活动
    @RequestMapping(value = "/workbench/clue/unbund.do")
    public @ResponseBody Integer unbund(String id){
        Integer result = clueService.unbundActivityAndClue(id);

        return result;

    }


    //获取数据字典
    @RequestMapping(value = "/workbench/clue/dicStageList.do")
    public @ResponseBody Object dicStageList(String stage){
        Map<String,String> map = new HashMap();
        map.put("stage",stage);
        Map<String,List<DicValue>> retMap = dicService.getDicValueSet(map);
        return retMap;
    }

}
