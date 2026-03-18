package com.revplay.userservice.controller;

import com.revplay.userservice.common.response.ApiResponse;
import com.revplay.userservice.dto.request.UpdateProfileRequest;
import com.revplay.userservice.dto.response.UserProfileResponse;
import com.revplay.userservice.security.AuthContextUtil;
import com.revplay.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    private final UserService userService;

    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserProfile(userId), "Profile retrieved"));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest profile) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(userId, profile), "Profile updated"));
    }
}
