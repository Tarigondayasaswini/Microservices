package com.revplay.playbackservice.service.impl;

import com.revplay.playbackservice.config.FileStorageProperties;
import com.revplay.playbackservice.exception.ResourceNotFoundException;
import com.revplay.playbackservice.service.SongFileResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.nio.file.Path;

@Component
public class LocalSongFileResolver implements SongFileResolver {

    private final FileStorageProperties fileStorageProperties;

    public LocalSongFileResolver(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public Resource loadSongResource(String fileUrl) {
        String fileName = extractFileName(fileUrl);
        Path path = Path.of(fileStorageProperties.getBaseDir(), fileStorageProperties.getSongsDir(), fileName);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Song file not found");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Song file not found");
        }
    }

    private String extractFileName(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            throw new ResourceNotFoundException("Song file not found");
        }
        int idx = fileUrl.lastIndexOf('/');
        if (idx < 0 || idx == fileUrl.length() - 1) {
            return fileUrl; // Handle case where fileUrl is just the filename
        }
        return fileUrl.substring(idx + 1);
    }
}
