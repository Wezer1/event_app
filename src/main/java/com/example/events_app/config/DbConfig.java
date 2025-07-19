package com.example.events_app.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.sql.DataSource;

//@Configuration
//public class DbConfig {
//
//    @Bean
//    public DataSource dataSource() {
//        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/trpo");
//        dataSourceBuilder.username("user");
//        dataSourceBuilder.password("user");
//        dataSourceBuilder.driverClassName("org.postgresql.Driver");
//        return dataSourceBuilder.build();
//    }
//}

//@Configuration
//public class DbConfig {
//
//    @Bean
//    public DataSource dataSource() {
//        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/events_app");
//        dataSourceBuilder.username("postgres");
//        dataSourceBuilder.password("123456");
//        dataSourceBuilder.driverClassName("org.postgresql.Driver");
//        return dataSourceBuilder.build();
//    }
//
//}

@Configuration
public class DbConfig {

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/events_app");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("postgres");
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        return dataSourceBuilder.build();
    }

}

//@Configuration
//public class DbConfig {
//
//    @Bean
//    public DataSource dataSource() {
//        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/trpo");
//        dataSourceBuilder.username("postgres");
//        dataSourceBuilder.password("20040725");
//        dataSourceBuilder.driverClassName("org.postgresql.Driver");
//        return dataSourceBuilder.build();
//    }
//
//}