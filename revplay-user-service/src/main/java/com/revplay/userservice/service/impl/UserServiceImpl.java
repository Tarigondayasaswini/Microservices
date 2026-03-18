package com.revplay.userservice.service.impl;

import com.revplay.userservice.dto.request.UpdateProfileRequest;
import com.revplay.userservice.dto.response.UserProfileResponse;
import com.revplay.userservice.dto.response.UserResponse;
import com.revplay.userservice.entity.User;
import com.revplay.userservice.entity.UserProfile;
import com.revplay.userservice.exception.AuthNotFoundException;
import com.revplay.userservice.repository.UserProfileRepository;
import com.revplay.userservice.repository.UserRepository;
import com.revplay.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserServiceImpl(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthNotFoundException("User profile not found"));
        return mapToUserProfileResponse(profile);
    }

    @Transactional
    @Override
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest requestProfile) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthNotFoundException("User profile not found"));

        if (requestProfile.fullName() != null)
            profile.setFullName(requestProfile.fullName());
        if (requestProfile.bio() != null)
            profile.setBio(requestProfile.bio());
        if (requestProfile.profilePictureUrl() != null)
            profile.setProfilePictureUrl(requestProfile.profilePictureUrl());
        if (requestProfile.country() != null)
            profile.setCountry(requestProfile.country());

        profile.setUpdatedAt(Instant.now());

        UserProfile savedProfile = userProfileRepository.save(profile);
        return mapToUserProfileResponse(savedProfile);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    private UserProfileResponse mapToUserProfileResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getUserId(),
                profile.getFullName(),
                profile.getBio(),
                profile.getProfilePictureUrl(),
                profile.getCountry()
        );
    }
}
