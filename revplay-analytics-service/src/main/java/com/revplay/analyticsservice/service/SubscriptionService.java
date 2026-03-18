package com.revplay.analyticsservice.service;

public interface SubscriptionService {
    void createSubscription(Long userId, String planType);

    void cancelSubscription(Long userId);

    boolean hasActivePremium(Long userId);
}
