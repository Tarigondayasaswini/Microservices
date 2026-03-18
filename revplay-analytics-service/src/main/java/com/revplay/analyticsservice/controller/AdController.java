package com.revplay.analyticsservice.controller;

import com.revplay.analyticsservice.common.ApiResponse;
import com.revplay.analyticsservice.dto.AdCampaignDto;
import com.revplay.analyticsservice.dto.CreateAdCampaignRequest;
import com.revplay.analyticsservice.service.AdService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ads")
public class AdController {

    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdCampaignDto>> createCampaign(
            @Valid @RequestBody CreateAdCampaignRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adService.createCampaign(request), "Ad campaign created"));
    }

    @GetMapping("/serve")
    public ResponseEntity<ApiResponse<AdCampaignDto>> serveAd() {
        AdCampaignDto ad = adService.getRandomActiveAd();
        if (ad == null) {
            return ResponseEntity.ok(ApiResponse.success(null, "No active ads available at the moment"));
        }
        return ResponseEntity.ok(ApiResponse.success(ad, "Ad served"));
    }

    @PostMapping("/{campaignId}/impression")
    public ResponseEntity<ApiResponse<Void>> recordImpression(@PathVariable Long campaignId) {
        adService.recordImpression(campaignId);
        return ResponseEntity.ok(ApiResponse.success(null, "Impression recorded"));
    }

    @PostMapping("/{campaignId}/click")
    public ResponseEntity<ApiResponse<Void>> recordClick(@PathVariable Long campaignId) {
        adService.recordClick(campaignId);
        return ResponseEntity.ok(ApiResponse.success(null, "Click recorded"));
    }

    @DeleteMapping("/{campaignId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateCampaign(@PathVariable Long campaignId) {
        adService.deactivateCampaign(campaignId);
        return ResponseEntity.ok(ApiResponse.success(null, "Campaign deactivated"));
    }
}
