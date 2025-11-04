package com.maitrunghau.hotelbookingsystem.config.cloudinary;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryProperties {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
    private String folder;
    private boolean secure = true;
    private CloudinaryDefaultImage defaults = new CloudinaryDefaultImage();
}
