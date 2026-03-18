package com.revplay.playlistservice.service.impl;

import com.revplay.playlistservice.service.ContentReferenceValidationService;
import com.revplay.playlistservice.exception.BadRequestException;
import com.revplay.playlistservice.exception.DuplicateResourceException;
import com.revplay.playlistservice.exception.ResourceNotFoundException;
import com.revplay.playlistservice.dto.response.SystemPlaylistResponse;
import com.revplay.playlistservice.entity.SystemPlaylist;
import com.revplay.playlistservice.entity.SystemPlaylistSong;
import com.revplay.playlistservice.repository.SystemPlaylistRepository;
import com.revplay.playlistservice.repository.SystemPlaylistSongRepository;
import com.revplay.playlistservice.service.SystemPlaylistService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemPlaylistServiceImpl implements SystemPlaylistService {

    private final SystemPlaylistRepository systemPlaylistRepository;
    private final SystemPlaylistSongRepository systemPlaylistSongRepository;
    private final ContentReferenceValidationService contentReferenceValidationService;

    public SystemPlaylistServiceImpl(
            SystemPlaylistRepository systemPlaylistRepository,
            SystemPlaylistSongRepository systemPlaylistSongRepository,
            ContentReferenceValidationService contentReferenceValidationService
    ) {
        this.systemPlaylistRepository = systemPlaylistRepository;
        this.systemPlaylistSongRepository = systemPlaylistSongRepository;
        this.contentReferenceValidationService = contentReferenceValidationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemPlaylistResponse> getAllActivePlaylists() {
        return systemPlaylistRepository.findByIsActiveTrueAndDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getSongIdsBySlug(String slug) {
        SystemPlaylist playlist = systemPlaylistRepository.findBySlugAndDeletedAtIsNull(slug)
                .filter(SystemPlaylist::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("System playlist", slug));

        return systemPlaylistSongRepository.findBySystemPlaylist_IdAndDeletedAtIsNullOrderByPositionAsc(playlist.getId())
                .stream()
                .map(SystemPlaylistSong::getSongId)
                .toList();
    }

    @Override
    @Transactional
    public void addSongsBySlug(String slug, List<Long> songIds) {
        if (songIds == null || songIds.isEmpty()) {
            throw new BadRequestException("songIds must not be empty");
        }
        Set<Long> uniqueSongIds = new HashSet<>(songIds);
        if (uniqueSongIds.size() != songIds.size()) {
            throw new BadRequestException("songIds contains duplicates");
        }

        SystemPlaylist playlist = systemPlaylistRepository.findBySlugAndDeletedAtIsNull(slug)
                .filter(SystemPlaylist::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException("System playlist", slug));

        List<SystemPlaylistSong> existingSongs = systemPlaylistSongRepository
                .findBySystemPlaylist_IdAndDeletedAtIsNullOrderByPositionAsc(playlist.getId());
        int nextPosition = existingSongs.isEmpty() ? 1 : existingSongs.get(existingSongs.size() - 1).getPosition() + 1;

        for (Long songId : songIds) {
            contentReferenceValidationService.validateSongExists(songId);
            if (systemPlaylistSongRepository.existsBySystemPlaylist_IdAndSongIdAndDeletedAtIsNull(playlist.getId(), songId)) {
                throw new DuplicateResourceException("Song already exists in system playlist: " + songId);
            }

            SystemPlaylistSong mapping = new SystemPlaylistSong();
            mapping.setSystemPlaylist(playlist);
            mapping.setSongId(songId);
            mapping.setPosition(nextPosition++);
            systemPlaylistSongRepository.save(mapping);
        }
    }

    @Override
    @Transactional
    public void softDeletePlaylist(String slug) {
        SystemPlaylist playlist = systemPlaylistRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ResourceNotFoundException("System playlist", slug));
        playlist.setIsActive(false);
        playlist.setDeletedAt(LocalDateTime.now());
        systemPlaylistRepository.save(playlist);
    }

    private SystemPlaylistResponse toResponse(SystemPlaylist playlist) {
        return SystemPlaylistResponse.builder()
                .id(playlist.getId())
                .name(playlist.getName())
                .slug(playlist.getSlug())
                .description(playlist.getDescription())
                .build();
    }
}
