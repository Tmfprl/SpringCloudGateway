package org.example.web_mng_authentication.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:/application.yaml")
@EnableTransactionManagement
public class DBCconfig {
    @Bean(name = "postDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public DataSource postDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
