package com.revplay.playlistservice.integration.analytics;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "analytics-service")
public interface AnalyticsClient {

    @PostMapping("/api/v1/audit-logs")
    void logAction(@RequestBody Map<String, Object> request);
}
