package com.mahashakti.mahashaktiBE.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "consumption")
@Data
public class ConsumptionProperties {
    private final Map<String, BigDecimal> adult;
}
