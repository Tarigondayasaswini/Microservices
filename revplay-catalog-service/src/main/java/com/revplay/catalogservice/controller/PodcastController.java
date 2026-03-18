package com.revplay.catalogservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import com.revplay.catalogservice.constants.ApiPaths;
import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.request.PodcastCreateRequest;
import com.revplay.catalogservice.dto.request.PodcastUpdateRequest;
import com.revplay.catalogservice.dto.response.PodcastResponse;
import com.revplay.catalogservice.service.PodcastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import com.revplay.catalogservice.exception.PlaybackValidationException;
import com.revplay.catalogservice.exception.DiscoveryValidationException;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Podcasts", description = "Podcast management for artists")
public class PodcastController {
    private final PodcastService service;

    @PostMapping(ApiPaths.PODCASTS)
    @Operation(summary = "Create podcast")
    public ResponseEntity<ApiResponse<PodcastResponse>> create(@Validated @RequestBody PodcastCreateRequest request) {
        PodcastResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(response, "Podcast created"));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MissingServletRequestPartException.class,
            MissingServletRequestParameterException.class,
            HttpMediaTypeNotSupportedException.class,
            PlaybackValidationException.class,
            DiscoveryValidationException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequestGeneral(Exception ex) {
        log.warn("Bad Request (General): {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }

    @PutMapping(ApiPaths.PODCASTS + "/{podcastId}")
    @Operation(summary = "Update podcast")
    public ResponseEntity<ApiResponse<PodcastResponse>> update(@PathVariable Long podcastId,
                                                               @Validated @RequestBody PodcastUpdateRequest request) {
        PodcastResponse response = service.update(podcastId, request);
        return ResponseEntity.ok(success(response, "Podcast updated"));
    }

    @GetMapping(ApiPaths.PODCASTS + "/{podcastId}")
    @Operation(summary = "Get podcast")
    public ResponseEntity<ApiResponse<PodcastResponse>> get(@PathVariable Long podcastId) {
        PodcastResponse response = service.get(podcastId);
        return ResponseEntity.ok(success(response, "Podcast fetched"));
    }

    @DeleteMapping(ApiPaths.PODCASTS + "/{podcastId}")
    @Operation(summary = "Delete podcast")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long podcastId) {
        service.delete(podcastId);
        return ResponseEntity.ok(success(null, "Podcast deleted"));
    }

    @GetMapping(ApiPaths.ARTISTS + "/{artistId}/podcasts/list")
    @Operation(summary = "List podcasts by artist - internal redirect")
    public ResponseEntity<ApiResponse<Page<PodcastResponse>>> listByArtist(@PathVariable Long artistId,
                                                                            Pageable pageable) {
        Page<PodcastResponse> response = service.listByArtist(artistId, pageable);
        return ResponseEntity.ok(success(response, "Podcasts fetched"));
    }

    @GetMapping(ApiPaths.PODCASTS + "/recommended")
    @Operation(summary = "List recommended podcasts")
    public ResponseEntity<ApiResponse<Page<PodcastResponse>>> listRecommended(Pageable pageable) {
        Page<PodcastResponse> response = service.listRecommended(pageable);
        return ResponseEntity.ok(success(response, "Recommended podcasts fetched"));
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
