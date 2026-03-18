package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.request.SongCreateRequest;
import com.revplay.catalogservice.dto.request.SongUpdateRequest;
import com.revplay.catalogservice.dto.response.SongResponse;
import com.revplay.catalogservice.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SongResponse>> getSong(@PathVariable Long id) {
        SongResponse response = songService.getSongById(id);
        return ResponseEntity.ok(success(response, "Song retrieved"));
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ApiResponse<List<SongResponse>>> getSongsByArtist(@PathVariable Long artistId) {
        List<SongResponse> response = songService.getSongsByArtistId(artistId);
        return ResponseEntity.ok(success(response, "Artist songs retrieved"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SongResponse>>> searchSongs(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<SongResponse> response = songService.searchSongs(query, PageRequest.of(page, size));
        return ResponseEntity.ok(success(response, "Search successful"));
    }

    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<SongResponse>> createSong(
            @RequestPart("metadata") String metadataJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
        
        SongCreateRequest request = objectMapper.readValue(metadataJson, SongCreateRequest.class);
        
        if (file != null && !file.isEmpty()) {
            String fileUrl = songService.uploadAudioFile(file);
            request.setFileUrl(fileUrl);
        }
        
        SongResponse response = songService.createSong(request);
        return ResponseEntity.ok(success(response, "Song created"));
    }

    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SongResponse>> updateSong(@PathVariable Long id,
            @Valid @RequestBody SongUpdateRequest request) {
        SongResponse response = songService.updateSong(id, request);
        return ResponseEntity.ok(success(response, "Song updated"));
    }

    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok(success(null, "Song deleted"));
    }

    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAudio(@RequestParam("file") MultipartFile file) {
        String url = songService.uploadAudioFile(file);
        return ResponseEntity.ok(success(Map.of("fileUrl", url), "File uploaded successfully"));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<ApiResponse<Boolean>> isSongActive(@PathVariable Long id) {
        Boolean exists = songService.isSongActive(id);
        return ResponseEntity.ok(success(exists, "Song existence check"));
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
