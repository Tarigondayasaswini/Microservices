package com.revplay.catalogservice.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {
    private String baseDir = "/app/uploads";
    private String songsDir = "songs";
    private String podcastsDir = "podcasts";
    private String imagesDir = "images";
    private String userUploadDir = "uploads";
    private String systemUploadDir = "system";
    private String defaultAvatar = "default.png";
}
