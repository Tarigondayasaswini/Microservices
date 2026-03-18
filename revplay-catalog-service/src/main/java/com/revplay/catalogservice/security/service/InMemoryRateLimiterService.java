package com.revplay.catalogservice.security.service;
public interface InMemoryRateLimiterService {
    boolean consume(String key, int limit, int window);
}
