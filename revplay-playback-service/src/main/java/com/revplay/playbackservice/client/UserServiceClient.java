package com.revplay.playbackservice.client;

import com.revplay.playbackservice.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}/premium")
    ApiResponse<Boolean> isUserPremium(@PathVariable("id") Long id);
}
