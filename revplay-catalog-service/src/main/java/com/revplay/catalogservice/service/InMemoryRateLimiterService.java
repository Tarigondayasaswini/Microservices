package com.revplay.catalogservice.service;

public interface InMemoryRateLimiterService {

    void ensureWithinLimit(String key, int maxRequests, int windowSeconds, String message);
}


