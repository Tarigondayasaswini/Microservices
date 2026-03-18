package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.request.GenreUpsertRequest;
import com.revplay.catalogservice.dto.response.GenreResponse;
import com.revplay.catalogservice.service.GenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/genres")
@Tag(name = "Genres", description = "Genre master data management APIs")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    @Operation(summary = "Get all active genres")
    public ResponseEntity<ApiResponse<List<GenreResponse>>> getAll() {
        log.info("Received request to fetch all genres");
        List<GenreResponse> response = genreService.getAll();
        return ResponseEntity.ok(success(response, "Genres fetched"));
    }

    @GetMapping("/{genreId}")
    @Operation(summary = "Get genre by id")
    public ResponseEntity<ApiResponse<GenreResponse>> getById(@PathVariable Long genreId) {
        log.info("Received request to fetch genre by id: {}", genreId);
        GenreResponse response = genreService.getById(genreId);
        return ResponseEntity.ok(success(response, "Genre fetched"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create genre (Admin)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ApiResponse<GenreResponse>> create(@Valid @RequestBody GenreUpsertRequest request) {
        log.info("Received request to create genre");
        GenreResponse response = genreService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(response, "Genre created"));
    }

    @PutMapping("/{genreId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update genre (Admin)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ApiResponse<GenreResponse>> update(
            @PathVariable Long genreId,
            @Valid @RequestBody GenreUpsertRequest request
    ) {
        log.info("Received request to update genre id: {}", genreId);
        GenreResponse response = genreService.update(genreId, request);
        return ResponseEntity.ok(success(response, "Genre updated"));
    }

    @DeleteMapping("/{genreId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete genre (Admin, soft delete)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long genreId) {
        log.info("Received request to delete genre id: {}", genreId);
        genreService.delete(genreId);
        return ResponseEntity.ok(success(null, "Genre deleted"));
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
