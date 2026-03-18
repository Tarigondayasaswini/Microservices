package com.revplay.analyticsservice.controller;

import com.revplay.analyticsservice.common.ApiResponse;
import com.revplay.analyticsservice.security.AuthContextUtil;
import com.revplay.analyticsservice.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<Void>> subscribe(@RequestParam(defaultValue = "PREMIUM") String planType) {
        Long userId = AuthContextUtil.getCurrentUserId();
        subscriptionService.createSubscription(userId, planType);
        return ResponseEntity.ok(ApiResponse.success(null, "Successfully subscribed to " + planType));
    }

    @PostMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel() {
        Long userId = AuthContextUtil.getCurrentUserId();
        subscriptionService.cancelSubscription(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Subscription cancelled"));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkPremiumStatus() {
        Long userId = AuthContextUtil.getCurrentUserId();
        boolean isPremium = subscriptionService.hasActivePremium(userId);
        return ResponseEntity.ok(ApiResponse.success(isPremium, "Premium status checked"));
    }
}
