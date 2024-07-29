package com.mahashakti.mahashaktiBE.config;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "datasource")
@Component
@Data
public class PsqlConfigProperties {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
    private Hikari hikari;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Hikari {
        private int maximumPoolSize;
        private int minimumPoolSize;
    }
}
