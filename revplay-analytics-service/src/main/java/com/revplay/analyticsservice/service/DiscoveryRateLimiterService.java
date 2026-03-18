package com.revplay.analyticsservice.service;

public interface DiscoveryRateLimiterService {

    void ensureWithinLimit(String key, int maxRequests, int windowSeconds, String message);
}


