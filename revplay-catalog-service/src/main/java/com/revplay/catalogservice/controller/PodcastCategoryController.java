package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.constants.ApiPaths;
import com.revplay.catalogservice.dto.request.PodcastCategoryCreateRequest;
import com.revplay.catalogservice.dto.response.PodcastCategoryResponse;
import com.revplay.catalogservice.service.PodcastCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.PODCAST_CATEGORIES)
@Tag(name = "Podcast Categories", description = "Podcast category management")
public class PodcastCategoryController {
    private final PodcastCategoryService service;

    @PostMapping
    @Operation(summary = "Create podcast category")
    public ResponseEntity<ApiResponse<PodcastCategoryResponse>> create(@Valid @RequestBody PodcastCategoryCreateRequest request) {
        PodcastCategoryResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(response, "Podcast category created"));
    }

    @GetMapping
    @Operation(summary = "List podcast categories")
    public ResponseEntity<ApiResponse<List<PodcastCategoryResponse>>> list() {
        List<PodcastCategoryResponse> response = service.list();
        return ResponseEntity.ok(success(response, "Podcast categories fetched"));
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
