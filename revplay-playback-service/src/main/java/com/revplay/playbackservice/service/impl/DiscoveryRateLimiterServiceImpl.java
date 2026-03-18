package com.revplay.playbackservice.service.impl;

import com.revplay.playbackservice.exception.DiscoveryValidationException;
import com.revplay.playbackservice.service.DiscoveryRateLimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DiscoveryRateLimiterServiceImpl implements DiscoveryRateLimiterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryRateLimiterServiceImpl.class);

    private final Map<String, Deque<Instant>> requestWindows = new ConcurrentHashMap<>();

    @Override
    public void ensureWithinLimit(String key, int maxRequests, int windowSeconds, String message) {
        LOGGER.debug("Checking discovery rate limit key={}", key);
        Instant now = Instant.now();
        Deque<Instant> window = requestWindows.computeIfAbsent(key, ignored -> new ArrayDeque<>());
        synchronized (window) {
            Instant threshold = now.minusSeconds(windowSeconds);
            while (!window.isEmpty() && window.peekFirst().isBefore(threshold)) {
                window.pollFirst();
            }
            if (window.size() >= maxRequests) {
                throw new DiscoveryValidationException(message);
            }
            window.addLast(now);
        }
    }
}
