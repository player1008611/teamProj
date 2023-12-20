package com.teamProj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // (exclude = {SecurityAutoConfiguration.class})
public class TeamProjApplication {
    public static void main(String[] args) {
        SpringApplication.run(TeamProjApplication.class, args);
    }
}
