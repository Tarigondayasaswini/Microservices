package com.revplay.playbackservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file-storage")
public class FileStorageProperties {
    private String baseDir = "./uploads";
    private String songsDir = "songs";
}
