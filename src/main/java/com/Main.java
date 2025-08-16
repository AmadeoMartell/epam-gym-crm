package com;

import com.epam.crm.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;


@Slf4j
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        ctx.registerShutdownHook();

        log.info("Application started. Context id = {}", ctx.getId());

        log.debug("Users: {}", ctx.getBean("users", HashMap.class));
        log.debug("trainees: {}", ctx.getBean("trainees", HashMap.class));
        log.debug("trainers: {}", ctx.getBean("trainers", HashMap.class));
        log.debug("training types: {}", ctx.getBean("trainingTypes", HashMap.class));
        log.debug("trainings: {}", ctx.getBean("trainings", HashMap.class));
    }
}