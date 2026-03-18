package com.revplay.playlistservice.controller;

import com.revplay.playlistservice.common.response.ApiResponse;
import com.revplay.playlistservice.dto.request.CreatePlaylistRequest;
import com.revplay.playlistservice.dto.request.UpdatePlaylistRequest;
import com.revplay.playlistservice.dto.response.PlaylistDetailResponse;
import com.revplay.playlistservice.dto.response.PlaylistResponse;
import com.revplay.playlistservice.dto.response.PlaylistSongResponse;
import com.revplay.playlistservice.security.AuthContextUtil;
import com.revplay.playlistservice.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final AuthContextUtil authContextUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<PlaylistResponse>> createPlaylist(@Valid @RequestBody CreatePlaylistRequest request) {
        Long userId = authContextUtil.getCurrentUserId();
        return ResponseEntity
                .ok(ApiResponse.success(playlistService.createPlaylist(userId, request), "Playlist created"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PlaylistResponse>>> getMyPlaylists() {
        Long userId = authContextUtil.getCurrentUserId();
        return ResponseEntity
                .ok(ApiResponse.success(playlistService.getUserPlaylists(userId), "User playlists retrieved"));
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<ApiResponse<PlaylistDetailResponse>> getPlaylist(@PathVariable Long playlistId) {
        Long userId = authContextUtil.getCurrentUserId();
        return ResponseEntity
                .ok(ApiResponse.success(playlistService.getPlaylistById(userId, playlistId), "Playlist retrieved"));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<PlaylistResponse>>> getPublicPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(playlistService.getPublicPlaylists(pageable), "Public playlists retrieved"));
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PlaylistResponse>>> searchPlaylists(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(playlistService.searchPlaylists(keyword, pageable), "Playlists found"));
    }

    @PutMapping("/{playlistId}")
    public ResponseEntity<ApiResponse<PlaylistResponse>> updatePlaylist(@PathVariable Long playlistId,
            @Valid @RequestBody UpdatePlaylistRequest request) {
        Long userId = authContextUtil.getCurrentUserId();
        return ResponseEntity.ok(
                ApiResponse.success(playlistService.updatePlaylist(userId, playlistId, request), "Playlist updated"));
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<ApiResponse<Void>> deletePlaylist(@PathVariable Long playlistId) {
        Long userId = authContextUtil.getCurrentUserId();
        playlistService.deletePlaylist(userId, playlistId);
        return ResponseEntity.ok(ApiResponse.success(null, "Playlist deleted"));
    }

    @PostMapping("/{playlistId}/songs")
    public ResponseEntity<ApiResponse<PlaylistSongResponse>> addSongToPlaylist(@PathVariable Long playlistId,
            @RequestBody java.util.Map<String, Long> payload) {
        Long userId = authContextUtil.getCurrentUserId();
        Long songId = payload.get("songId");
        if (songId == null) {
            throw new IllegalArgumentException("songId is required");
        }
        return ResponseEntity.ok(ApiResponse.success(playlistService.addSongToPlaylist(userId, playlistId, songId),
                "Song added to playlist"));
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<ApiResponse<Void>> removeSongFromPlaylist(@PathVariable Long playlistId,
            @PathVariable Long songId) {
        Long userId = authContextUtil.getCurrentUserId();
        playlistService.removeSongFromPlaylist(userId, playlistId, songId);
        return ResponseEntity.ok(ApiResponse.success(null, "Song removed from playlist"));
    }
}
