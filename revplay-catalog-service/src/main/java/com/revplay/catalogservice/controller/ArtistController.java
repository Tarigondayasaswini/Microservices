package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.request.ArtistCreateRequest;
import com.revplay.catalogservice.dto.request.ArtistUpdateRequest;
import com.revplay.catalogservice.dto.response.ArtistResponse;
import com.revplay.catalogservice.security.AuthContextUtil;
import com.revplay.catalogservice.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import com.revplay.catalogservice.dto.response.ArtistSummaryResponse;
import com.revplay.catalogservice.dto.response.SongResponse;
import com.revplay.catalogservice.dto.response.PodcastResponse;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArtistResponse>> getArtist(@PathVariable Long id) {
        ArtistResponse response = artistService.getArtistById(id);
        return ResponseEntity.ok(success(response, "Artist retrieved"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ArtistResponse>> getArtistByUserId(@PathVariable Long userId) {
        ArtistResponse response = artistService.getArtistByUserId(userId);
        return ResponseEntity.ok(success(response, "Artist profile retrieved"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ArtistResponse>>> searchArtists(
            @RequestParam String query,
            Pageable pageable) {
        Page<ArtistResponse> response = artistService.searchArtists(query, pageable);
        return ResponseEntity.ok(success(response, "Search results"));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ApiResponse<ArtistResponse>> createArtistProfile(@Valid @RequestBody ArtistCreateRequest request) {
        Long userId = AuthContextUtil.getCurrentUserId();
        ArtistResponse response = artistService.createArtist(userId, request);
        return ResponseEntity.ok(success(response, "Artist profile created"));
    }

    @PreAuthorize("hasRole('ARTIST') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArtistResponse>> updateArtistProfile(
            @PathVariable Long id,
            @Valid @RequestBody ArtistUpdateRequest request) {
        ArtistResponse response = artistService.updateArtist(id, request);
        return ResponseEntity.ok(success(response, "Artist profile updated"));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<ArtistSummaryResponse>> getArtistSummary(@PathVariable Long id) {
        ArtistSummaryResponse response = artistService.getArtistSummary(id);
        return ResponseEntity.ok(success(response, "Artist summary retrieved"));
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<ApiResponse<Page<SongResponse>>> getArtistSongs(
            @PathVariable Long id,
            org.springframework.data.domain.Pageable pageable) {
        Page<SongResponse> response = artistService.getArtistSongs(id, pageable);
        return ResponseEntity.ok(success(response, "Artist songs retrieved"));
    }

    @GetMapping("/{id}/podcasts")
    public ResponseEntity<ApiResponse<Page<PodcastResponse>>> getArtistPodcasts(
            @PathVariable Long id,
            org.springframework.data.domain.Pageable pageable) {
        Page<PodcastResponse> response = artistService.getArtistPodcasts(id, pageable);
        return ResponseEntity.ok(success(response, "Artist podcasts retrieved"));
    }

    private <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
