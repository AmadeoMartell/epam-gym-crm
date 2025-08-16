package com.epam.crm.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @PostConstruct
    public void initLogging() {
        LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(ctx);
        encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg\n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
        console.setContext(ctx);
        console.setEncoder(encoder);
        console.start();

        Logger root = ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.detachAndStopAllAppenders();
        root.setLevel(Level.INFO);
        root.addAppender(console);

        ctx.getLogger("org.springframework").setLevel(Level.WARN);
        ctx.getLogger("org.springframework.beans").setLevel(Level.ERROR);
        ctx.getLogger("org.springframework.context").setLevel(Level.WARN);

        ctx.getLogger("com.epam.crm").setLevel(Level.DEBUG);
    }
}
