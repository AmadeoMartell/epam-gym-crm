package com.epam.crm.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Import({LoggingConfig.class})
@ComponentScan("com.epam.crm")
@EnableTransactionManagement
@PropertySources(@PropertySource("classpath:/application.properties"))
@EnableJpaRepositories(basePackages = "com.epam.crm.repository")
public class ApplicationConfig {
    private final Environment env;
    public ApplicationConfig(Environment env) { this.env = env; }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getRequiredProperty("db.driver"));
        ds.setUrl(env.getRequiredProperty("db.url"));
        ds.setUsername(env.getRequiredProperty("db.username"));
        ds.setPassword(env.getRequiredProperty("db.password"));
        return ds;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(ds);
        emf.setPackagesToScan("com.epam.crm.model");
        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
        vendor.setShowSql(Boolean.parseBoolean(env.getProperty("jpa.show-sql", "false")));
        emf.setJpaVendorAdapter(vendor);

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", env.getProperty("jpa.ddl-auto", "validate"));
        props.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        props.put("hibernate.format_sql", env.getProperty("jpa.format-sql", "true"));
        if (env.getProperty("hibernate.jdbc.time_zone") != null)
            props.put("hibernate.jdbc.time_zone", env.getProperty("hibernate.jdbc.time_zone"));
        emf.setJpaProperties(props);
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
