package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.dto.PagedResponseDto;
import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.constants.ApiPaths;
import com.revplay.catalogservice.dto.request.SearchRequest;
import com.revplay.catalogservice.dto.response.SearchResultItemResponse;
import com.revplay.catalogservice.enums.SearchContentType;
import com.revplay.catalogservice.integration.playlist.PlaylistResponse;
import com.revplay.catalogservice.integration.playlist.PlaylistSearchService;
import com.revplay.catalogservice.security.service.DiscoveryRateLimiterService;
import com.revplay.catalogservice.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.API_V1 + "/search")
@Tag(name = "Search", description = "Search and advanced filtering APIs")
public class SearchController {

    private final SearchService searchService;
    private final PlaylistSearchService playlistSearchService;
    private final DiscoveryRateLimiterService discoveryRateLimiterService;

    @GetMapping
    @Operation(summary = "Search across songs, albums, artists, podcasts and episodes")
    public ResponseEntity<ApiResponse<PagedResponseDto<SearchResultItemResponse>>> search(
            @RequestParam("q") String query,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "releaseDateFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateFrom,
            @RequestParam(value = "releaseDateTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate releaseDateTo,
            @RequestParam(value = "artistType", required = false) String artistType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "releaseDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir,
            HttpServletRequest httpServletRequest
    ) {
        log.info("Received search request with query={}, type={}, page={}, size={}", query, type, page, size);
        String clientKey = resolveClientKey(httpServletRequest);
        discoveryRateLimiterService.ensureWithinLimit(
                "search:" + clientKey,
                60,
                60,
                "Too many search requests. Please try again later."
        );
        SearchRequest request = new SearchRequest(
                query,
                SearchContentType.from(type),
                genreId,
                releaseDateFrom,
                releaseDateTo,
                artistType,
                page,
                size,
                sortBy,
                sortDir
        );
        PagedResponseDto<SearchResultItemResponse> response = searchService.search(request);
        return ResponseEntity.ok(success(response, "Search results fetched"));
    }

    @GetMapping("/playlists")
    @Operation(summary = "Search public playlists by keyword")
    public ResponseEntity<ApiResponse<PagedResponseDto<PlaylistResponse>>> searchPlaylists(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String query = q != null ? q : keyword;
        log.info("Received playlist search request, query={}, page={}, size={}", query, page, size);
        if (query == null || query.isBlank()) {
            return ResponseEntity.ok(success(PagedResponseDto.empty(page, size, "playlist", "query"), "Empty keyword"));
        }
        PagedResponseDto<PlaylistResponse> response = playlistSearchService.searchPublicPlaylists(query, page, size);
        return ResponseEntity.ok(success(response, "Playlist search results fetched"));
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        String remote = request.getRemoteAddr();
        return remote == null || remote.isBlank() ? "unknown" : remote;
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
