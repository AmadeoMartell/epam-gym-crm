package com;

import com.epam.crm.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@Slf4j
public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        ctx.registerShutdownHook();

        log.info("Application started. Context id = {}", ctx.getId());
    }
}