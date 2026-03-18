package com.revplay.analyticsservice.controller;

import com.revplay.analyticsservice.common.ApiResponse;
import com.revplay.analyticsservice.security.AuthContextUtil;
import com.revplay.analyticsservice.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Alias controller for /premium/** endpoints called by the frontend.
 * The actual subscription logic is in SubscriptionController at /subscriptions/**.
 */
@RestController
@RequestMapping("/api/v1/premium")
public class PremiumController {

    private final SubscriptionService subscriptionService;

    public PremiumController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * GET /api/v1/premium/status?userId={id}
     * Frontend calls this to check if the logged-in user has premium access.
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkPremiumStatus(
            @RequestParam(required = false) Long userId) {
        Long resolvedUserId = userId;
        if (resolvedUserId == null || resolvedUserId <= 0) {
            resolvedUserId = AuthContextUtil.getCurrentUserId();
        }
        if (resolvedUserId == null || resolvedUserId <= 0) {
            return ResponseEntity.ok(ApiResponse.success(false, "Not authenticated"));
        }
        boolean isPremium = subscriptionService.hasActivePremium(resolvedUserId);
        return ResponseEntity.ok(ApiResponse.success(isPremium, "Premium status checked"));
    }

    /**
     * POST /api/v1/premium/upgrade  - subscribe to premium
     */
    @PostMapping("/upgrade")
    public ResponseEntity<ApiResponse<Void>> upgradeToPremium(
            @RequestParam(name = "planType", required = false) String planType,
            @RequestParam(name = "plan", required = false) String plan,
            @RequestParam(name = "userId", required = false) Long userId) {
        
        Long resolvedUserId = userId;
        if (resolvedUserId == null || resolvedUserId <= 0) {
            resolvedUserId = AuthContextUtil.getCurrentUserId();
        }
        
        if (resolvedUserId == null) {
             return ResponseEntity.status(401).body(ApiResponse.error("User not authenticated"));
        }

        String resolvedPlan = planType != null ? planType : (plan != null ? plan : "MONTHLY");
        subscriptionService.createSubscription(resolvedUserId, resolvedPlan);
        return ResponseEntity.ok(ApiResponse.success(null, "Successfully upgraded to " + resolvedPlan));
    }
}
