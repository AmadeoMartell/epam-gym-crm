package com.epam.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories
public class EpamCrmApplication {
    public static void main(String[] args) {
        SpringApplication.run(EpamCrmApplication.class, args);
    }
}