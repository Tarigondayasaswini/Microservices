package com.revplay.userservice.controller;

import com.revplay.userservice.common.response.ApiResponse;
import com.revplay.userservice.dto.response.UserResponse;
import com.revplay.userservice.dto.request.UpdateProfileRequest;
import com.revplay.userservice.dto.response.UserProfileResponse;
import com.revplay.userservice.security.AuthContextUtil;
import com.revplay.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        Long currentUserId = AuthContextUtil.getCurrentUserId();
        return ResponseEntity
                .ok(ApiResponse.success(userService.getCurrentUser(currentUserId), "Current user retrieved"));
    }

    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        Long currentUserId = AuthContextUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(userService.getUserProfile(currentUserId), "Profile retrieved"));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@RequestBody UpdateProfileRequest profile) {
        Long currentUserId = AuthContextUtil.getCurrentUserId();
        return ResponseEntity
                .ok(ApiResponse.success(userService.updateProfile(currentUserId, profile), "Profile updated"));
    }

    // This would be secured by Gateway / SecurityConfig restricting to Admin or
    // internal Feign calls
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(userId), "User retrieved"));
    }
}
