package com.revplay.analyticsservice.controller;

import com.revplay.analyticsservice.common.ApiResponse;
import com.revplay.analyticsservice.dto.AuditLogRequest;
import com.revplay.analyticsservice.entity.AuditLog;
import com.revplay.analyticsservice.security.AuthContextUtil;
import com.revplay.analyticsservice.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> logAction(@Valid @RequestBody AuditLogRequest request,
            HttpServletRequest httpRequest) {
        Long userId = AuthContextUtil.getCurrentUserId();
        // Fallback for getting IP for audit trails. Note: Gateway forwards real IP in
        // headers in production config.
        String ipAddress = httpRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null)
            ipAddress = httpRequest.getRemoteAddr();

        auditService.logAction(userId, request, ipAddress);
        return ResponseEntity.ok(ApiResponse.success(null, "Audit record created"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getMyLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Long userId = AuthContextUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                auditService.getUserLogs(userId, PageRequest.of(page, size)), "User audit logs retrieved"));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getUserLogsAsAdmin(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                auditService.getUserLogs(userId, PageRequest.of(page, size)), "User audit logs retrieved"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                auditService.getAllLogs(PageRequest.of(page, size)), "All audit logs retrieved"));
    }
}
