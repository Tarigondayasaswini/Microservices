package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.security.service.InMemoryRateLimiterService;
import com.revplay.catalogservice.exception.AuthValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRateLimiterServiceImpl implements InMemoryRateLimiterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryRateLimiterServiceImpl.class);

    private final Map<String, Deque<Instant>> requestWindows = new ConcurrentHashMap<>();

    @Override
    public boolean consume(String key, int limit, int window) {
        try {
            ensureWithinLimit(key, limit, window, "Rate limit exceeded");
            return true;
        } catch (AuthValidationException ex) {
            return false;
        }
    }

    public void ensureWithinLimit(String key, int maxRequests, int windowSeconds, String message) {
        LOGGER.debug("Checking auth rate limit key={}", key);
        Instant now = Instant.now();
        Deque<Instant> window = requestWindows.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (window) {
            Instant threshold = now.minusSeconds(windowSeconds);
            while (!window.isEmpty() && window.peekFirst().isBefore(threshold)) {
                window.pollFirst();
            }
            if (window.size() >= maxRequests) {
                throw new AuthValidationException(message);
            }
            window.addLast(now);
        }
    }
}


