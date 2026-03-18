package com.revplay.playlistservice.service;

import com.revplay.playlistservice.dto.response.LikedSongResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikedSongService {
    void likeSong(Long userId, Long songId);

    void unlikeSong(Long userId, Long songId);

    boolean isSongLikedBy(Long userId, Long songId);

    Page<LikedSongResponse> getUserLikedSongs(Long userId, Pageable pageable);
}
