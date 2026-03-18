package com.revplay.playlistservice.service.impl;

import com.revplay.playlistservice.dto.response.LikedSongResponse;
import com.revplay.playlistservice.entity.LikedSong;
import com.revplay.playlistservice.repository.LikedSongRepository;
import com.revplay.playlistservice.service.ContentReferenceValidationService;
import com.revplay.playlistservice.service.LikedSongService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikedSongServiceImpl implements LikedSongService {

    private final LikedSongRepository likedSongRepository;
    private final ContentReferenceValidationService validationService;

    @Transactional
    @Override
    public void likeSong(Long userId, Long songId) {
        if (!likedSongRepository.existsByUserIdAndSongId(userId, songId)) {
            validationService.validateSongExists(songId); // cross-service call

            LikedSong likedSong = new LikedSong();
            likedSong.setUserId(userId);
            likedSong.setSongId(songId);
            likedSongRepository.save(likedSong);
        }
    }

    @Transactional
    @Override
    public void unlikeSong(Long userId, Long songId) {
        likedSongRepository.findByUserIdAndSongId(userId, songId)
                .ifPresent(likedSongRepository::delete);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isSongLikedBy(Long userId, Long songId) {
        return likedSongRepository.existsByUserIdAndSongId(userId, songId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<LikedSongResponse> getUserLikedSongs(Long userId, Pageable pageable) {
        return likedSongRepository.findByUserIdOrderByLikedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    private LikedSongResponse mapToResponse(LikedSong likedSong) {
        return LikedSongResponse.builder()
                .id(likedSong.getId())
                .userId(likedSong.getUserId())
                .songId(likedSong.getSongId())
                .likedAt(likedSong.getLikedAt())
                .build();
    }
}
