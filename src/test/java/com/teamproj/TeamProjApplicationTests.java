package com.teamproj;

import com.teamproj.dao.AdministratorDao;
import com.teamproj.domain.Administrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class TeamProjApplicationTests {
    @Autowired
    DataSource dataSource;

    @Autowired
    AdministratorDao administratorDao;

    @Test
    void getAll() {
        List<Administrator> administratorList = administratorDao.selectList(null);
        System.out.println(administratorList);
    }

    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getConnection());
    }

}
