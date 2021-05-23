package com.evo.crm.workbench.web.controller;

import com.evo.crm.settings.domain.User;
import com.evo.crm.settings.service.UserService;
import com.evo.crm.utils.DateTimeUtil;
import com.evo.crm.utils.RedisUtil;
import com.evo.crm.utils.UUIDUtil;
import com.evo.crm.workbench.domain.DicValue;
import com.evo.crm.workbench.domain.Tran;
import com.evo.crm.workbench.domain.TranHistory;
import com.evo.crm.workbench.service.CustomerService;
import com.evo.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TranController {
    @Autowired
    private TranService tranService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private RedisUtil redisUtil;


    //加载创建交易页面数据（取出）
    @RequestMapping(value = "/workbench/transaction/display.do")
    public @ResponseBody Map<String,List> display(String stage, String source,String transactionType){
        Map<String,String> map = new HashMap();
        map.put("stage",stage);
        map.put("source",source);
        map.put("transactionType",transactionType);

        Map<String,List> reMap = tranService.display(map);

        return reMap;
    }


    //取得客户名称列表（模糊查询）
    @RequestMapping(value = "/workbench/transaction/getCustomerName.do")
    public @ResponseBody List<String> getCustomerName(String name){
        List<String> list = customerService.getCustomerName(name);
        return list;
    }


    //添加交易
    @RequestMapping(value = "/workbench/transaction/save.do")
    public ModelAndView save(Tran tran, String customerName, HttpServletRequest request){
        tran.setCreateBy((((User)request.getSession().getAttribute("user")).getName()));
        tran.setId(UUIDUtil.getUUID());
        tran.setCreateTime(DateTimeUtil.getSysTime());

        ModelAndView mv = new ModelAndView();
        boolean flag = tranService.addTran(tran,customerName);
        if(flag){
            mv.setViewName("forward:/workbench/transaction/index.jsp");
        }

        return mv;
    }




    //跳转到详细信息页
    @RequestMapping(value = "/workbench/transaction/detail.do")
    public ModelAndView detail(String id,HttpServletRequest request){
        ModelAndView mv = new ModelAndView();
        Tran tran = tranService.queryById(id);
        //处理可能性
        String stage = tran.getStage();
        ServletContext application = request.getServletContext();
        Map<String,String> map = (Map<String, String>) application.getAttribute("pMap");
        String possibility = map.get(stage);
        tran.setPossibility(possibility);
        List<DicValue> dicValueList = redisUtil.getDicValueByCode("stage");


        mv.addObject("dicValueList",dicValueList);
        mv.addObject("tran",tran);

        mv.setViewName("/workbench/transaction/detail.jsp");
        return mv;
    }




    //取得当前交易的交易历史列表
    @RequestMapping(value = "/workbench/transaction/history.do")
    public @ResponseBody List<TranHistory> history(String tranId,HttpServletRequest request){
        //调用service获取交易历史列表
        List<TranHistory> historyList = tranService.queryHistoryListByTranId(tranId);

        //处理可能性
        ServletContext application = request.getServletContext();
        //取出全局作用域中的pMap（阶段与可能性对应关系）
        Map<String,String> map = (Map<String, String>) application.getAttribute("pMap");

        for(TranHistory history : historyList){
            String stage = history.getStage();
            String possibility = map.get(stage);
            //将可能性封装到对象中
            history.setPossibility(possibility);
        }
        return historyList;
    }




    //改变阶段  添加交易历史,给前端返回TranHistory对象
    @RequestMapping(value = "/workbench/transaction/changeStage.do")
    public @ResponseBody Map<String,Object> tranHistory(Tran tran,HttpServletRequest request){
        tran.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        tran.setEditTime(DateTimeUtil.getSysTime());
        //处理可能性
        ServletContext application = request.getServletContext();
        //取出全局作用域中的pMap（阶段与可能性对应关系）
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        //封装可能性
        tran.setPossibility(pMap.get(tran.getStage()));


        boolean flag = tranService.changeStage(tran);
        Map<String,Object> map = new HashMap<>();
        map.put("success",flag);
        map.put("t",tran);

        return map;
    }






    //给交易阶段数量统计漏斗图提供数据
    @RequestMapping(value = "/workbench/transaction/getCharts.do")
    public @ResponseBody Map<String,Object> charts() {
        Map<String,Object> map = tranService.getCharts();

        return map;
    }

}
