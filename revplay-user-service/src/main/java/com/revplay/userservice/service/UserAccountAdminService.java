package com.revplay.userservice.service;

import com.revplay.userservice.common.dto.PagedResponseDto;
import com.revplay.userservice.dto.request.UpdateUserRoleRequest;
import com.revplay.userservice.dto.request.UpdateUserStatusRequest;
import com.revplay.userservice.dto.response.AdminUserDetailsResponse;
import com.revplay.userservice.dto.response.SimpleMessageResponse;

import java.util.Optional;

public interface UserAccountAdminService {

    PagedResponseDto<AdminUserDetailsResponse> listUsers(int page, int size);

    Optional<AdminUserDetailsResponse> getUserById(Long userId);

    SimpleMessageResponse updateStatus(Long userId, UpdateUserStatusRequest request);

    SimpleMessageResponse updateRole(Long userId, UpdateUserRoleRequest request);
}
