package com.revplay.analyticsservice.controller;

import com.revplay.analyticsservice.dto.response.BusinessOverviewResponse;
import com.revplay.analyticsservice.dto.response.ConversionRateResponse;
import com.revplay.analyticsservice.dto.response.RevenueAnalyticsResponse;
import com.revplay.analyticsservice.dto.response.TopDownloadResponse;
import com.revplay.analyticsservice.dto.response.TopMixResponse;
import com.revplay.analyticsservice.service.AdminBusinessAnalyticsService;
import com.revplay.analyticsservice.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/business-analytics")
@Tag(name = "Admin Business Analytics", description = "Admin-level business analytics APIs")
public class AdminBusinessAnalyticsController {

    private final AdminBusinessAnalyticsService adminBusinessAnalyticsService;

    public AdminBusinessAnalyticsController(AdminBusinessAnalyticsService adminBusinessAnalyticsService) {
        this.adminBusinessAnalyticsService = adminBusinessAnalyticsService;
    }

    @GetMapping("/overview")
    @Operation(summary = "Get business overview metrics")
    public ResponseEntity<ApiResponse<BusinessOverviewResponse>> getOverview() {
        return ResponseEntity.ok(ApiResponse.success(
                adminBusinessAnalyticsService.getBusinessOverview(),
                "Business overview fetched"
        ));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue analytics")
    public ResponseEntity<ApiResponse<RevenueAnalyticsResponse>> getRevenue() {
        return ResponseEntity.ok(ApiResponse.success(
                adminBusinessAnalyticsService.getRevenueAnalytics(),
                "Revenue analytics fetched"
        ));
    }

    @GetMapping("/top-downloads")
    @Operation(summary = "Get top downloaded songs")
    public ResponseEntity<ApiResponse<List<TopDownloadResponse>>> getTopDownloads(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                adminBusinessAnalyticsService.getTopDownloadedSongs(limit),
                "Top downloaded songs fetched"
        ));
    }

    @GetMapping("/top-mixes")
    @Operation(summary = "Get top mixes by total play count")
    public ResponseEntity<ApiResponse<List<TopMixResponse>>> getTopMixes() {
        return ResponseEntity.ok(ApiResponse.success(
                adminBusinessAnalyticsService.getTopMixes(),
                "Top mixes fetched"
        ));
    }

    @GetMapping("/conversion-rate")
    @Operation(summary = "Get premium conversion rate")
    public ResponseEntity<ApiResponse<ConversionRateResponse>> getConversionRate() {
        return ResponseEntity.ok(ApiResponse.success(
                adminBusinessAnalyticsService.getPremiumConversionRate(),
                "Premium conversion rate fetched"
        ));
    }
}

