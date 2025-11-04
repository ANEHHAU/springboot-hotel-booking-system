

package com.maitrunghau.hotelbookingsystem.config.cloudinary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "cloudinary.defaults")
@Getter @Setter
public class CloudinaryDefaultImage {
    private String avatarCustomer = "";
    private String avatarAdmin    = "";
    private String roomImage      = "";
    private String banner         = "";
}
// "" tránh null fall back