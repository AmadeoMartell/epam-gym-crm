package com.epam.crm.config;

import org.springframework.context.annotation.*;

@Configuration
@Import({StorageConfig.class, LoggingConfig.class})
@ComponentScan("com.epam.crm")
@PropertySources(@PropertySource("classpath:/application.properties"))
public class ApplicationConfig {
}
