package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import com.revplay.catalogservice.constants.ApiPaths;
import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.request.PodcastEpisodeCreateRequest;
import com.revplay.catalogservice.dto.request.PodcastEpisodeUpdateRequest;
import com.revplay.catalogservice.dto.response.PodcastEpisodeResponse;
import com.revplay.catalogservice.service.PodcastEpisodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.PODCASTS + "/{podcastId}/episodes")
@Tag(name = "Podcast Episodes", description = "Podcast episode management")
public class PodcastEpisodeController {
    private final PodcastEpisodeService service;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Create podcast episode with audio upload")
    public ResponseEntity<ApiResponse<PodcastEpisodeResponse>> create(@PathVariable Long podcastId,
                                                                       @RequestPart("metadata") String metadata,
                                                                       @RequestPart("file") MultipartFile file) {
        PodcastEpisodeCreateRequest request = parseEpisodeMetadata(metadata);
        PodcastEpisodeResponse response = service.create(podcastId, request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(response, "Episode created"));
    }

    @PutMapping("/{episodeId}")
    @Operation(summary = "Update podcast episode")
    public ResponseEntity<ApiResponse<PodcastEpisodeResponse>> update(@PathVariable Long podcastId,
                                                                       @PathVariable Long episodeId,
                                                                       @Valid @RequestBody PodcastEpisodeUpdateRequest request) {
        PodcastEpisodeResponse response = service.update(podcastId, episodeId, request);
        return ResponseEntity.ok(success(response, "Episode updated"));
    }

    @GetMapping("/{episodeId}")
    @Operation(summary = "Get podcast episode")
    public ResponseEntity<ApiResponse<PodcastEpisodeResponse>> get(@PathVariable Long podcastId,
                                                                    @PathVariable Long episodeId) {
        PodcastEpisodeResponse response = service.get(podcastId, episodeId);
        return ResponseEntity.ok(success(response, "Episode fetched"));
    }

    @DeleteMapping("/{episodeId}")
    @Operation(summary = "Delete podcast episode")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long podcastId, @PathVariable Long episodeId) {
        service.delete(podcastId, episodeId);
        return ResponseEntity.ok(success(null, "Episode deleted"));
    }

    @GetMapping
    @Operation(summary = "List episodes by podcast")
    public ResponseEntity<ApiResponse<Page<PodcastEpisodeResponse>>> list(@PathVariable Long podcastId,
                                                                           Pageable pageable) {
        Page<PodcastEpisodeResponse> response = service.listByPodcast(podcastId, pageable);
        return ResponseEntity.ok(success(response, "Episodes fetched"));
    }

    @PutMapping(value = "/{episodeId}/audio", consumes = "multipart/form-data")
    @Operation(summary = "Replace podcast episode audio")
    public ResponseEntity<ApiResponse<PodcastEpisodeResponse>> replaceAudio(@PathVariable Long podcastId,
                                                                             @PathVariable Long episodeId,
                                                                             @RequestPart("file") MultipartFile file) {
        PodcastEpisodeResponse response = service.replaceAudio(podcastId, episodeId, file);
        return ResponseEntity.ok(success(response, "Episode audio replaced"));
    }

    private <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private PodcastEpisodeCreateRequest parseEpisodeMetadata(String metadata) {
        try {
            return objectMapper.readValue(metadata, PodcastEpisodeCreateRequest.class);
        } catch (Exception exception) {
            throw new BadRequestException("Invalid episode metadata JSON");
        }
    }
}
