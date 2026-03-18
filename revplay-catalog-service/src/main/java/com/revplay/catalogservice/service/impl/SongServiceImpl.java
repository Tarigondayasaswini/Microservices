package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.dto.request.SongCreateRequest;
import com.revplay.catalogservice.dto.request.SongUpdateRequest;
import com.revplay.catalogservice.dto.response.SongResponse;
import com.revplay.catalogservice.entity.Song;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.mapper.SongMapper;
import com.revplay.catalogservice.repository.SongRepository;
import com.revplay.catalogservice.service.SongService;
import com.revplay.catalogservice.util.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public SongResponse createSong(SongCreateRequest request) {
        Song song = songMapper.toEntity(request, request.getArtistId(), request.getFileUrl());
        Song saved = songRepository.save(song);
        return songMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public SongResponse updateSong(Long id, SongUpdateRequest request) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", id));

        songMapper.updateEntity(song, request);
        Song updated = songRepository.save(song);
        return songMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public void deleteSong(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", id));
        song.setIsActive(false);
        songRepository.save(song);
    }

    @Transactional(readOnly = true)
    @Override
    public SongResponse getSongById(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song", id));
        return songMapper.toResponse(song);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SongResponse> getSongsByArtistId(Long artistId) {
        return songRepository.findByArtistId(artistId).stream()
                .filter(Song::getIsActive)
                .map(songMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SongResponse> searchSongs(String query, Pageable pageable) {
        return songRepository.findByTitleContainingIgnoreCaseAndVisibilityAndIsActiveTrue(query, "PUBLIC", pageable)
                .map(songMapper::toResponse);
    }

    /**
     * Stores audio via FileStorageService (under uploads/songs/) and returns the
     * canonical API path /api/v1/files/songs/{fileName}.
     * FileController.getSong() serves this path with byte-range support,
     * which is required for the player timer to advance correctly.
     */
    @Override
    public String uploadAudioFile(MultipartFile file) {
        String storedFileName = fileStorageService.storeSong(file);
        return "/api/v1/files/songs/" + storedFileName;
    }

    @Override
    public boolean isSongActive(Long id) {
        return songRepository.existsBySongIdAndIsActiveTrue(id);
    }
}
