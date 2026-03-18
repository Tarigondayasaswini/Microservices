package com.revplay.userservice.service.impl;

import com.revplay.userservice.common.dto.PagedResponseDto;
import com.revplay.userservice.dto.request.UpdateUserRoleRequest;
import com.revplay.userservice.dto.request.UpdateUserStatusRequest;
import com.revplay.userservice.dto.response.AdminUserDetailsResponse;
import com.revplay.userservice.dto.response.SimpleMessageResponse;
import com.revplay.userservice.entity.User;
import com.revplay.userservice.enums.UserRole;
import com.revplay.userservice.exception.AuthNotFoundException;
import com.revplay.userservice.repository.UserRepository;
import com.revplay.userservice.service.UserAccountAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserAccountAdminServiceImpl implements UserAccountAdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountAdminServiceImpl.class);

    private final UserRepository userRepository;

    public UserAccountAdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<AdminUserDetailsResponse> listUsers(int page, int size) {
        LOGGER.info("Listing users for admin page={} size={}", page, size);
        Page<AdminUserDetailsResponse> userPage = userRepository.findAll(PageRequest.of(page, size))
                .map(this::toAdminUserDetailsResponse);
        return PagedResponseDto.of(userPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AdminUserDetailsResponse> getUserById(Long userId) {
        LOGGER.info("Fetching admin user details for userId={}", userId);
        return userRepository.findById(userId)
                .map(this::toAdminUserDetailsResponse);
    }

    @Transactional
    @Override
    public SimpleMessageResponse updateStatus(Long userId, UpdateUserStatusRequest request) {
        LOGGER.info("Updating account status for userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthNotFoundException("User not found"));

        user.setIsActive(request.isActive());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return new SimpleMessageResponse("Account status updated");
    }

    @Transactional
    @Override
    public SimpleMessageResponse updateRole(Long userId, UpdateUserRoleRequest request) {
        LOGGER.info("Updating role for userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthNotFoundException("User not found"));

        try {
            UserRole newRole = UserRole.valueOf(request.role().toUpperCase());
            user.setRole(newRole);
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
            return new SimpleMessageResponse("User role updated");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role specified");
        }
    }

    private AdminUserDetailsResponse toAdminUserDetailsResponse(User user) {
        return new AdminUserDetailsResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                Boolean.TRUE.equals(user.getIsActive()) ? "ACTIVE" : "INACTIVE",
                user.getCreatedAt()
        );
    }
}
