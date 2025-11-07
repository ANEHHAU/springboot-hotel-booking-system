package com.maitrunghau.hotelbookingsystem.config.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String baseUrl;
    private int emailVerifyExpireMinutes = 0;
    private int otpExpireMinutes = 0;
}
