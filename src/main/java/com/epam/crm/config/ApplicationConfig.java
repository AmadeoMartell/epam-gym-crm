package com.epam.crm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan("com.epam.crm")
@PropertySources(@PropertySource("classpath:/application.properties"))
public class ApplicationConfig {
}
