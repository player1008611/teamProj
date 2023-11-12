package com.teamProj.service;

import com.teamProj.utils.HttpResult;

public interface AdministratorService {
    HttpResult administratorLogin(String account, String password);

    HttpResult administratorLogout();
}
