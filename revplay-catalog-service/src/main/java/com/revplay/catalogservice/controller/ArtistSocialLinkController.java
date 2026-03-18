package com.revplay.catalogservice.controller;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.dto.request.ArtistSocialLinkRequest;
import com.revplay.catalogservice.dto.response.ArtistSocialLinkResponse;
import com.revplay.catalogservice.service.ArtistSocialLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/artists/{artistId}/links", "/api/v1/artists/{artistId}/social-links"})
@Tag(name = "Artist Social Links", description = "Management of artist social media links")
public class ArtistSocialLinkController {

    private final ArtistSocialLinkService socialLinkService;

    @GetMapping
    @Operation(summary = "Get all social links for an artist")
    public ResponseEntity<ApiResponse<List<ArtistSocialLinkResponse>>> getByArtist(@PathVariable Long artistId) {
        List<ArtistSocialLinkResponse> response = socialLinkService.getLinksByArtistId(artistId);
        return ResponseEntity.ok(success(response, "Social links fetched"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ARTIST') or hasRole('ADMIN')")
    @Operation(summary = "Add or update a social link")
    public ResponseEntity<ApiResponse<ArtistSocialLinkResponse>> upsert(
            @PathVariable Long artistId,
            @Valid @RequestBody ArtistSocialLinkRequest request
    ) {
        ArtistSocialLinkResponse response = socialLinkService.upsertLink(artistId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(success(response, "Social link updated"));
    }

    @DeleteMapping("/{linkId}")
    @PreAuthorize("hasRole('ARTIST') or hasRole('ADMIN')")
    @Operation(summary = "Delete a social link")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long artistId, @PathVariable Long linkId) {
        socialLinkService.deleteLink(artistId, linkId);
        return ResponseEntity.ok(success(null, "Social link deleted"));
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
