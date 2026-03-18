package com.revplay.playlistservice.controller;

import com.revplay.playlistservice.common.response.ApiResponse;
import com.revplay.playlistservice.dto.response.LikedSongResponse;
import com.revplay.playlistservice.security.AuthContextUtil;
import com.revplay.playlistservice.service.LikedSongService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
public class LikedSongController {

    private final LikedSongService likedSongService;
    private final AuthContextUtil authContextUtil;

    public LikedSongController(LikedSongService likedSongService, AuthContextUtil authContextUtil) {
        this.likedSongService = likedSongService;
        this.authContextUtil = authContextUtil;
    }

    @PostMapping("/songs/{songId}")
    public ResponseEntity<ApiResponse<Void>> likeSong(@PathVariable Long songId) {
        Long userId = authContextUtil.getCurrentUserId();
        likedSongService.likeSong(userId, songId);
        return ResponseEntity.ok(ApiResponse.success(null, "Song liked"));
    }

    @DeleteMapping("/songs/{songId}")
    public ResponseEntity<ApiResponse<Void>> unlikeSong(@PathVariable Long songId) {
        Long userId = authContextUtil.getCurrentUserId();
        likedSongService.unlikeSong(userId, songId);
        return ResponseEntity.ok(ApiResponse.success(null, "Song unliked"));
    }

    @GetMapping("/songs")
    public ResponseEntity<ApiResponse<Page<LikedSongResponse>>> getLikedSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = authContextUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                likedSongService.getUserLikedSongs(userId, PageRequest.of(page, size)), "Liked songs retrieved"));
    }

    @GetMapping("/songs/{songId}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkIsLiked(@PathVariable Long songId) {
        Long userId = authContextUtil.getCurrentUserId();
        return ResponseEntity
                .ok(ApiResponse.success(likedSongService.isSongLikedBy(userId, songId), "Like status checked"));
    }
}
