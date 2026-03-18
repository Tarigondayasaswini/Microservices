package com.revplay.analyticsservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String baseDir = "uploads";
    private String adsDir = "ads";
}
