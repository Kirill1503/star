package com.starbank.star.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import liquibase.integration.spring.SpringLiquibase;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase secondLiquibase(@Qualifier("secondDataSource") DataSource secondDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(secondDataSource);
        liquibase.setChangeLog("classpath:liquibase/changelog-master.yml");
        liquibase.setContexts("second-db");
        liquibase.setDefaultSchema("public");
        return liquibase;
    }
}