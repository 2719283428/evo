package com.evo.crm.settings.service;

import com.evo.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String,Object> login(String loginAct, String loginPwd, String ip);
    List<User> getUserList();
}
