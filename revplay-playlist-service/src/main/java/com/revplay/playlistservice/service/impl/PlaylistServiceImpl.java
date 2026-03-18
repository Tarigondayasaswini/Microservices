package com.revplay.playlistservice.service.impl;

import com.revplay.playlistservice.dto.request.CreatePlaylistRequest;
import com.revplay.playlistservice.dto.request.UpdatePlaylistRequest;
import com.revplay.playlistservice.dto.response.PlaylistDetailResponse;
import com.revplay.playlistservice.dto.response.PlaylistResponse;
import com.revplay.playlistservice.dto.response.PlaylistSongResponse;
import com.revplay.playlistservice.entity.Playlist;
import com.revplay.playlistservice.entity.PlaylistSong;
import com.revplay.playlistservice.exception.ResourceNotFoundException;
import com.revplay.playlistservice.mapper.PlaylistMapper;
import com.revplay.playlistservice.repository.PlaylistFollowRepository;
import com.revplay.playlistservice.repository.PlaylistRepository;
import com.revplay.playlistservice.repository.PlaylistSongRepository;
import com.revplay.playlistservice.service.ContentReferenceValidationService;
import com.revplay.playlistservice.service.PlaylistService;
import com.revplay.playlistservice.security.AuthContextUtil;
import com.revplay.playlistservice.integration.analytics.AnalyticsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistFollowRepository playlistFollowRepository;
    private final ContentReferenceValidationService validationService;
    private final PlaylistMapper playlistMapper;
    private final AuthContextUtil authContextUtil;
    private final AnalyticsClient analyticsClient;

    private void logAudit(String action, String entityType, Long entityId, String details) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("action", action);
            request.put("entityType", entityType);
            request.put("entityId", entityId);
            request.put("details", details);
            analyticsClient.logAction(request);
        } catch (Exception e) {
            log.error("Failed to create audit log: {}", e.getMessage());
        }
    }

    @Transactional
    @Override
    public PlaylistResponse createPlaylist(Long userId, CreatePlaylistRequest request) {
        Playlist playlist = playlistMapper.toEntity(request, userId);
        Playlist saved = playlistRepository.save(playlist);
        
        logAudit("CREATE", "PLAYLIST", saved.getId(), "Created playlist: " + saved.getName());
        
        return playlistMapper.toResponse(saved, 0, 0);
    }

    @Transactional
    @Override
    public PlaylistResponse updatePlaylist(Long userId, Long playlistId, UpdatePlaylistRequest request) {
        Playlist playlist = getPlaylistForUser(userId, playlistId);
        playlistMapper.updateEntity(playlist, request);
        Playlist updated = playlistRepository.save(playlist);
        
        long songCount = playlistSongRepository.countByPlaylist_Id(playlistId);
        long followerCount = playlistFollowRepository.countByPlaylistId(playlistId);
        
        logAudit("UPDATE", "PLAYLIST", playlistId, "Updated playlist: " + updated.getName());
        
        return playlistMapper.toResponse(updated, songCount, followerCount);
    }

    @Transactional
    @Override
    public void deletePlaylist(Long userId, Long playlistId) {
        Playlist playlist = getPlaylistForUser(userId, playlistId);
        playlist.setIsActive(Boolean.FALSE);
        playlistRepository.save(playlist);
        
        logAudit("DELETE", "PLAYLIST", playlistId, "Deactivated playlist: " + playlist.getName());
    }

    @Transactional(readOnly = true)
    @Override
    public PlaylistDetailResponse getPlaylistById(Long userId, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));

        if (!Boolean.TRUE.equals(playlist.getIsActive())) {
            throw new ResourceNotFoundException("Playlist not found");
        }

        if (!Boolean.TRUE.equals(playlist.getIsPublic()) && !playlist.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Playlist not found"); // Masking unauthorized as not found
        }

        List<PlaylistSong> songs = playlistSongRepository.findByPlaylist_IdOrderByAddedAtDesc(playlistId);
        long songCount = songs.size();
        long followerCount = playlistFollowRepository.countByPlaylistId(playlistId);
        
        return playlistMapper.toDetailResponse(playlist, songs, songCount, followerCount);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PlaylistResponse> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(playlist -> {
                    long songCount = playlistSongRepository.countByPlaylist_Id(playlist.getId());
                    long followerCount = playlistFollowRepository.countByPlaylistId(playlist.getId());
                    return playlistMapper.toResponse(playlist, songCount, followerCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public PlaylistSongResponse addSongToPlaylist(Long userId, Long playlistId, Long songId) {
        Playlist playlist = getPlaylistForUser(userId, playlistId);

        return playlistSongRepository.findByPlaylist_IdAndSongId(playlistId, songId)
                .map(playlistMapper::toSongResponse)
                .orElseGet(() -> {
                    validationService.validateSongExists(songId); // cross-service call

                    PlaylistSong ps = new PlaylistSong();
                    ps.setPlaylist(playlist);
                    ps.setSongId(songId);
                    
                    // Set position
                    Integer lastPosition = playlistSongRepository.findMaxPositionByPlaylistId(playlistId);
                    ps.setPosition(lastPosition == null ? 0 : lastPosition + 1);
                    
                    PlaylistSong saved = playlistSongRepository.save(ps);
                    playlist.setUpdatedAt(LocalDateTime.now());
                    
                    logAudit("ADD_SONG", "PLAYLIST", playlistId, "Added song " + songId + " to playlist");
                    
                    return playlistMapper.toSongResponse(saved);
                });
    }

    @Transactional
    @Override
    public void removeSongFromPlaylist(Long userId, Long playlistId, Long songId) {
        Playlist playlist = getPlaylistForUser(userId, playlistId);
        playlistSongRepository.findByPlaylist_IdAndSongId(playlistId, songId)
                .ifPresent(ps -> {
                    playlistSongRepository.delete(ps);
                    playlist.setUpdatedAt(LocalDateTime.now());
                    logAudit("REMOVE_SONG", "PLAYLIST", playlistId, "Removed song " + songId + " from playlist");
                });
    }

    @Override
    public Page<PlaylistResponse> searchPlaylists(String keyword, Pageable pageable) {
        Long currentUserId = authContextUtil.getCurrentUserIdOrNull();
        boolean isAdmin = authContextUtil.hasRole("ADMIN");
        
        Page<Playlist> playlists;
        if (isAdmin) {
            playlists = playlistRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(keyword, pageable);
        } else if (currentUserId != null) {
            playlists = playlistRepository.searchPlaylistsForUser(keyword, currentUserId, pageable);
        } else {
            playlists = playlistRepository.searchPublicPlaylistsByKeyword(keyword, pageable);
        }

        return playlists.map(playlist -> PlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .userId(playlist.getUserId())
                .isPublic(playlist.getIsPublic())
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .build());
    }

    private Playlist getPlaylistForUser(Long userId, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
                
        // Allow Admins to bypass user ID match
        boolean isAdmin = authContextUtil.hasRole("ADMIN");
        
        if (!isAdmin && !playlist.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Playlist not found");
        }
        if (!Boolean.TRUE.equals(playlist.getIsActive())) {
             throw new ResourceNotFoundException("Playlist not found");
        }
        return playlist;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PlaylistResponse> getPublicPlaylists(Pageable pageable) {
        return playlistRepository.findByIsPublicTrueAndIsActiveTrue(pageable)
                .map((Function<Playlist, PlaylistResponse>) playlist -> {
                    long songCount = playlistSongRepository.countByPlaylist_Id(playlist.getId());
                    long followerCount = playlistFollowRepository.countByPlaylistId(playlist.getId());
                    return playlistMapper.toResponse(playlist, songCount, followerCount);
                });
    }
}
