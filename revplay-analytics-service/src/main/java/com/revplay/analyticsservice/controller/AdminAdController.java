package com.revplay.analyticsservice.controller;

import com.revplay.analyticsservice.entity.Ad;
import com.revplay.analyticsservice.service.AdminAdService;
import com.revplay.analyticsservice.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/ads")
@RequiredArgsConstructor
@Tag(name = "Admin Ads", description = "Admin ad upload APIs")
public class AdminAdController {

    private final AdminAdService adminAdService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload audio ad")
    public ResponseEntity<ApiResponse<Ad>> uploadAd(
            @RequestParam String title,
            @RequestParam(required = false) Integer durationSeconds,
            @RequestParam MultipartFile file
    ) {
        Ad saved = adminAdService.uploadAd(title, file, durationSeconds);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "Ad uploaded successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate ad")
    public ResponseEntity<ApiResponse<Ad>> deactivateAd(@PathVariable Long id) {
        Ad updated = adminAdService.deactivateAd(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "Ad deactivated successfully"));
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate ad")
    public ResponseEntity<ApiResponse<Ad>> activateAd(@PathVariable Long id) {
        Ad updated = adminAdService.activateAd(id);
        return ResponseEntity.ok(ApiResponse.success(updated, "Ad activated successfully"));
    }
}
