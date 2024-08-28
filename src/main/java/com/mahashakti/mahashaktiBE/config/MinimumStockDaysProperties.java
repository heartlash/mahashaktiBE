package com.mahashakti.mahashaktiBE.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "minimum-stock-days")
@Data
public class MinimumStockDaysProperties {

    private final Map<String, Integer> materials;

}
