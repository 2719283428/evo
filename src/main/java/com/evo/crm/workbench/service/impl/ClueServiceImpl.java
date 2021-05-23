package com.evo.crm.workbench.service.impl;

import com.evo.crm.utils.DateTimeUtil;
import com.evo.crm.utils.UUIDUtil;
import com.evo.crm.workbench.dao.*;
import com.evo.crm.workbench.domain.*;
import com.evo.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ClueServiceImpl implements ClueService {
    //线索相关表
    @Autowired
    private ClueDao clueDao;
    @Autowired
    private ClueActivityRelationDao clueActivityRelationDao;
    @Autowired
    private ClueRemarkDao clueRemarkDao;

    //客户相关表
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustomerRemarkDao customerRemarkDao;

    //联系人相关表
    @Autowired
    private ContactsDao contactsDao;
    @Autowired
    private ContactsRemarkDao contactsRemarkDao;
    @Autowired
    private ContactsActivityRelationDao contactsActivityRelationDao;

    //交易相关表
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;

    //解除关联的市场活动
    @Override
    public Integer unbundActivityAndClue(String id) {
        Integer result = clueActivityRelationDao.deleteActivityAndClue(id);
        return result;
    }

    //关联市场活动
    @Override
    public Integer bundActivityAndClue(String clueId, String[] activityIds) {
        Integer result = 0;
        for(String activityId:activityIds){
            String id = UUIDUtil.getUUID();

            ClueActivityRelation clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setId(id);
            clueActivityRelation.setClueId(clueId);
            clueActivityRelation.setActivityId(activityId);

            result = clueActivityRelationDao.insertAll(clueActivityRelation);
        }
        return result;
    }


    //线索转换业务
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT)
    @Override
    public boolean convert(String clueId, Tran tran, String createBy,Integer flagCon) {
        String createTime = DateTimeUtil.getSysTime();
        //设置标记
        boolean flag = true;



        //通过线索id获得线索对象
        Clue clue = clueDao.getClueById(clueId);
        //通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在）
        String company = clue.getCompany();
        Customer customer = customerDao.selectCustomerByName(company);
        //如果customer为null，说明以前没有这个客户，需要新建一个
        if(customer == null){
            customer =  new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setContactSummary(clue.getContactSummary());
            //添加客户
            Integer resultCustomer = customerDao.insertCustomer(customer);
            if(resultCustomer != 1){
                flag = false;
            }
        }

        //通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());

        contacts.setCustomerId(customer.getId());

        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());

        //添加联系人
        Integer resultContacts = contactsDao.insertContacts(contacts);
        if(resultContacts != 1){
            flag = false;
        }

        //线索备注转换到客户备注以及联系人备注
        //查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.selectByClueId(clueId);
        //取出每一条线索的备注
        for(ClueRemark clueRemark:clueRemarkList){
            //取出备注信息（主要转换到客户备注和联系人备注的就是这个备注信息）
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            Integer resultCustomerRemark = customerRemarkDao.insertCustomerRemark(customerRemark);
            if(resultCustomerRemark!=1){
                flag = false;
            }

            //创建联系人备注对象，添加联系人
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            Integer resultContactsRemark = contactsRemarkDao.insertContactsRemark(contactsRemark);
            if(resultContactsRemark!=1){
                flag = false;
            }
        }
        //“线索和市场活动”的关系转换到“联系人和市场活动”的关系
        //查询出与该条线索关联的市场活动，查询与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.selectListByClueId(clueId);
        //遍历出每一条与市场活动关联的关联关系记录
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            String activityId = clueActivityRelation.getActivityId();

            //创建 联系人与市场活动的关联关系对象 让第三步生成的联系人与市场活动做关联
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            //添加联系人与市场活动的关联关系
            Integer resultCAR = contactsActivityRelationDao.insert(contactsActivityRelation);
            if(resultCAR != 1){
                flag = false;
            }
        }


        //如果flagCon不等于null说明已添加交易
        if(null != flagCon){
            //继续完善对tran对象的封装

            //设置id
            tran.setId(UUIDUtil.getUUID());
            //设置创建人
            tran.setCreateBy(createBy);
            //设置创建时间
            tran.setCreateTime(createTime);

            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactSummary(clue.getContactSummary());
            tran.setContactsId(contacts.getId());
            //添加交易
            Integer resultTran = tranDao.insertTran(tran);
            if(resultTran != 1){
                flag = false;
            }

            //如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setTranId(tran.getId());

            //添加交易历史
            Integer resultHistory = tranHistoryDao.insertTranHistory(tranHistory);
            if(resultHistory != 1){
                flag = false;
            }
        }



        //删除线索备注
        for(ClueRemark clueRemark:clueRemarkList){
            Integer deleteClueRemark = clueRemarkDao.delete(clueRemark);
            if(deleteClueRemark != 1){
                flag = false;
            }
        }


        //删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            Integer deleteActivityAndClueResult = clueActivityRelationDao.deleteActivityAndClue(clueActivityRelation.getClueId());
            if(deleteActivityAndClueResult != 1){
                flag = false;
            }
        }


        //删除线索
        Integer deleteClueResult = clueDao.deleteClue(clueId);
        if(deleteClueResult != 1){
            flag = false;
        }



        return flag;
    }
}
