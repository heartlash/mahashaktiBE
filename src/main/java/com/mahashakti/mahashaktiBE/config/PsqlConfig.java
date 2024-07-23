package com.mahashakti.mahashaktiBE.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PsqlConfig {
    private final PsqlConfigProperties psqlEnvConfig;

    /**
     * Data source configuration
     *
     * @return Data source object
     */
    @Bean(name = "psqlDataSource")
    public DataSource dataSource() {
        log.info("Env config:-" + psqlEnvConfig.getUrl());
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(psqlEnvConfig.getDriverClassName());
        dataSource.setJdbcUrl(psqlEnvConfig.getUrl());
        dataSource.setUsername(psqlEnvConfig.getUsername());
        dataSource.setPassword(psqlEnvConfig.getPassword());
        dataSource.setMaximumPoolSize(psqlEnvConfig.getHikari().getMaximumPoolSize());
        dataSource.setMinimumIdle(psqlEnvConfig.getHikari().getMinimumPoolSize());
        return dataSource;
    }
}
