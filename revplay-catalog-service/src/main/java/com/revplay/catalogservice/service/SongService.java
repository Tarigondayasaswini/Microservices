package com.revplay.catalogservice.service;

import com.revplay.catalogservice.dto.request.SongCreateRequest;
import com.revplay.catalogservice.dto.request.SongUpdateRequest;
import com.revplay.catalogservice.dto.response.SongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongService {
    SongResponse createSong(SongCreateRequest request);

    SongResponse updateSong(Long id, SongUpdateRequest request);

    void deleteSong(Long id);

    SongResponse getSongById(Long id);

    List<SongResponse> getSongsByArtistId(Long artistId);

    Page<SongResponse> searchSongs(String query, Pageable pageable);

    String uploadAudioFile(MultipartFile file);

    boolean isSongActive(Long id);
}
