package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.constants.ApiPaths;
import com.revplay.catalogservice.dto.request.SongGenresRequest;
import com.revplay.catalogservice.service.SongGenreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPaths.SONGS + "/{songId}/genres")
@Tag(name = "Song Genres", description = "Song to genre assignment")
public class SongGenreController {
    private final SongGenreService service;

    @PostMapping
    @Operation(summary = "Add genres to song (additive)")
    public ResponseEntity<ApiResponse<Void>> assign(@PathVariable Long songId,
                                                    @Validated @RequestBody SongGenresRequest request) {
        service.addGenres(songId, request.getGenreIds());
        return ResponseEntity.ok(success("Genres added"));
    }

    @PutMapping
    @Operation(summary = "Replace song genres")
    public ResponseEntity<ApiResponse<Void>> replace(@PathVariable Long songId,
                                                     @Validated @RequestBody SongGenresRequest request) {
        service.replaceGenres(songId, request.getGenreIds());
        return ResponseEntity.ok(success("Genres updated"));
    }

    private <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
