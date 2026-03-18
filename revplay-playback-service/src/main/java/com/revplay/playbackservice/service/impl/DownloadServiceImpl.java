package com.revplay.playbackservice.service.impl;

import com.revplay.playbackservice.client.CatalogServiceClient;
import com.revplay.playbackservice.client.UserServiceClient;
import com.revplay.playbackservice.dto.response.ApiResponse;
import com.revplay.playbackservice.dto.response.SongResponse;
import com.revplay.playbackservice.security.AuthenticatedUserPrincipal;
import com.revplay.playbackservice.entity.SongDownload;
import com.revplay.playbackservice.exception.AccessDeniedException;
import com.revplay.playbackservice.exception.BadRequestException;
import com.revplay.playbackservice.exception.ResourceNotFoundException;
import com.revplay.playbackservice.repository.SongDownloadRepository;
import com.revplay.playbackservice.service.DownloadService;
import com.revplay.playbackservice.service.SongFileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DownloadServiceImpl implements DownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadServiceImpl.class);

    private final UserServiceClient userServiceClient;
    private final CatalogServiceClient catalogServiceClient;
    private final SongFileResolver songFileResolver;
    private final SongDownloadRepository songDownloadRepository;

    public DownloadServiceImpl(
            UserServiceClient userServiceClient,
            CatalogServiceClient catalogServiceClient,
            SongFileResolver songFileResolver,
            SongDownloadRepository songDownloadRepository
    ) {
        this.userServiceClient = userServiceClient;
        this.catalogServiceClient = catalogServiceClient;
        this.songFileResolver = songFileResolver;
        this.songDownloadRepository = songDownloadRepository;
    }

    @Override
    @Transactional
    public Resource downloadSong(Long userId, Long songId) {
        validateIds(userId, songId);

        ApiResponse<Boolean> premiumResponse = userServiceClient.isUserPremium(userId);
        if (premiumResponse == null || !Boolean.TRUE.equals(premiumResponse.getData())) {
            throw new AccessDeniedException("Premium subscription required to download songs");
        }

        ResponseEntity<ApiResponse<SongResponse>> response = catalogServiceClient.getSongById(songId);
        if (response == null || response.getBody() == null || response.getBody().getData() == null) {
            throw new ResourceNotFoundException("Song not found with id: " + songId);
        }
        
        SongResponse song = response.getBody().getData();
        Resource resource = songFileResolver.loadSongResource(song.getFileUrl());

        if (!songDownloadRepository.existsByUserIdAndSongId(userId, songId)) {
            SongDownload songDownload = new SongDownload();
            songDownload.setUserId(userId);
            songDownload.setSongId(songId);
            songDownload.setDownloadedAt(LocalDateTime.now());
            songDownloadRepository.save(songDownload);
            LOGGER.info("Song downloaded: userId={}, songId={}", userId, songId);
        }

        return resource;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDownloaded(Long userId, Long songId) {
        validateIds(userId, songId);
        return songDownloadRepository.existsByUserIdAndSongId(userId, songId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDownloadFileName(Long songId) {
        ResponseEntity<ApiResponse<SongResponse>> response = catalogServiceClient.getSongById(songId);
        if (response == null || response.getBody() == null || response.getBody().getData() == null) {
            throw new ResourceNotFoundException("Song not found with id: " + songId);
        }
        
        SongResponse song = response.getBody().getData();
        String title = (song.getTitle() == null || song.getTitle().isBlank()) ? ("song-" + songId) : song.getTitle().trim();
        String safeTitle = title.replaceAll("[^a-zA-Z0-9\\-_ ]", "").replace(' ', '-');
        return safeTitle + ".mp3";
    }

    private void validateIds(Long userId, Long songId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("userId is required");
        }
        if (songId == null || songId <= 0) {
            throw new BadRequestException("songId is required");
        }
    }
}
