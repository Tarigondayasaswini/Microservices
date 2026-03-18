package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.dto.PagedResponseDto;
import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.response.NewReleaseItemResponse;
import com.revplay.catalogservice.dto.response.PopularPodcastItemResponse;
import com.revplay.catalogservice.dto.response.SearchResultItemResponse;
import com.revplay.catalogservice.dto.response.TopArtistItemResponse;
import com.revplay.catalogservice.service.BrowseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/browse")
@Tag(name = "Browse", description = "Browse and category listing APIs")
public class BrowseController {

    private final BrowseService browseService;

    @GetMapping("/new-releases")
    @Operation(summary = "Browse new releases")
    public ResponseEntity<ApiResponse<PagedResponseDto<NewReleaseItemResponse>>> newReleases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        log.info("Received browse new releases request page={}, size={}", page, size);
        PagedResponseDto<NewReleaseItemResponse> response = browseService.newReleases(page, size, sortDir);
        return ResponseEntity.ok(success(response, "New releases fetched"));
    }

    @GetMapping("/top-artists")
    @Operation(summary = "Browse top artists")
    public ResponseEntity<ApiResponse<PagedResponseDto<TopArtistItemResponse>>> topArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Received browse top artists request page={}, size={}", page, size);
        PagedResponseDto<TopArtistItemResponse> response = browseService.topArtists(page, size);
        return ResponseEntity.ok(success(response, "Top artists fetched"));
    }

    @GetMapping("/popular-podcasts")
    @Operation(summary = "Browse popular podcasts")
    public ResponseEntity<ApiResponse<PagedResponseDto<PopularPodcastItemResponse>>> popularPodcasts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Received browse popular podcasts request page={}, size={}", page, size);
        PagedResponseDto<PopularPodcastItemResponse> response = browseService.popularPodcasts(page, size);
        return ResponseEntity.ok(success(response, "Popular podcasts fetched"));
    }

    @GetMapping("/songs")
    @Operation(summary = "Browse all songs")
    public ResponseEntity<ApiResponse<PagedResponseDto<SearchResultItemResponse>>> allSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        log.info("Received browse all songs request page={}, size={}", page, size);
        PagedResponseDto<SearchResultItemResponse> response = browseService.allSongs(page, size, sortBy, sortDir);
        return ResponseEntity.ok(success(response, "All songs fetched"));
    }

    @GetMapping("/genres/{genreId}/songs")
    @Operation(summary = "Browse songs by genre")
    public ResponseEntity<ApiResponse<PagedResponseDto<SearchResultItemResponse>>> songsByGenre(
            @PathVariable Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        log.info("Received browse songs by genre request genreId={}, page={}, size={}", genreId, page, size);
        PagedResponseDto<SearchResultItemResponse> response = browseService.songsByGenre(genreId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(success(response, "Songs by genre fetched"));
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
