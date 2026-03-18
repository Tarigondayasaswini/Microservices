package com.revplay.userservice.controller;

import com.revplay.userservice.common.dto.PagedResponseDto;
import com.revplay.userservice.common.response.ApiResponse;
import com.revplay.userservice.dto.request.UpdateUserRoleRequest;
import com.revplay.userservice.dto.request.UpdateUserStatusRequest;
import com.revplay.userservice.dto.response.AdminUserDetailsResponse;
import com.revplay.userservice.dto.response.SimpleMessageResponse;
import com.revplay.userservice.service.UserAccountAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserAccountAdminController {

    private final UserAccountAdminService userAccountAdminService;

    public UserAccountAdminController(UserAccountAdminService userAccountAdminService) {
        this.userAccountAdminService = userAccountAdminService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponseDto<AdminUserDetailsResponse>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                userAccountAdminService.listUsers(page, size),
                "Users retrieved"
        ));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserDetailsResponse>> getUserById(@PathVariable Long userId) {
        return userAccountAdminService.getUserById(userId)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user, "User details retrieved")))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> updateStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                userAccountAdminService.updateStatus(userId, request),
                "Status updated"
        ));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> updateRole(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                userAccountAdminService.updateRole(userId, request),
                "Role updated"
        ));
    }
}
