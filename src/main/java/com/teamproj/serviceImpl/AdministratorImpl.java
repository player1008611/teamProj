package com.teamproj.serviceImpl;

import com.teamproj.dao.AdministratorDao;
import com.teamproj.domain.Administrator;
import com.teamproj.service.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorImpl implements AdministratorService {
    @Autowired
    AdministratorDao administratorDao;
    @Override
    public String addAdmin() {
        administratorDao.addAdmin();
        return "addAdmin";
    }
}
