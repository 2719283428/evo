package com.evo.crm.settings.dao;

import com.evo.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    User login(Map<String, String> logMap);

    List<User> selectUserAll();
}
