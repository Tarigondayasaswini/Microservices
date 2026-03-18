package com.revplay.playlistservice.controller;

import com.revplay.playlistservice.common.response.ApiResponse;
import com.revplay.playlistservice.dto.request.AddSystemPlaylistSongsRequest;
import com.revplay.playlistservice.dto.response.SystemPlaylistResponse;
import com.revplay.playlistservice.service.SystemPlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system-playlists")
@RequiredArgsConstructor
@Tag(name = "System Mix Playlists", description = "Curated system mix playlists")
public class SystemPlaylistController {

    private final SystemPlaylistService systemPlaylistService;

    @GetMapping
    @Operation(summary = "Get all active system playlists")
    public ResponseEntity<ApiResponse<List<SystemPlaylistResponse>>> getAllActivePlaylists() {
        return ResponseEntity.ok(
                ApiResponse.success(systemPlaylistService.getAllActivePlaylists(), "System playlists retrieved")
        );
    }

    @GetMapping("/{slug}/songs")
    @Operation(summary = "Get ordered song IDs by playlist slug")
    public ResponseEntity<ApiResponse<List<Long>>> getSongIdsBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(
                ApiResponse.success(systemPlaylistService.getSongIdsBySlug(slug), "System playlist songs retrieved")
        );
    }

    @PostMapping("/{slug}/songs")
    @Operation(summary = "Add songs to system playlist by slug")
    public ResponseEntity<ApiResponse<Void>> addSongsBySlug(
            @PathVariable String slug,
            @Valid @RequestBody AddSystemPlaylistSongsRequest request
    ) {
        systemPlaylistService.addSongsBySlug(slug, request.songIds());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Songs added to system playlist"));
    }
}
