package com.revplay.userservice.service;

import com.revplay.userservice.dto.request.UpdateProfileRequest;
import com.revplay.userservice.dto.response.UserProfileResponse;
import com.revplay.userservice.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(Long userId);

    UserProfileResponse getUserProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    UserResponse getUserById(Long userId); // Admin or internal use
}
