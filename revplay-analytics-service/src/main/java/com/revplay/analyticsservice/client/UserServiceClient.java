package com.revplay.analyticsservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    ResponseEntity<Object> getUserById(@PathVariable("id") Long id);
}
