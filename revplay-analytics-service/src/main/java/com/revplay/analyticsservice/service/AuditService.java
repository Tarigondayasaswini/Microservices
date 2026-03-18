package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.dto.AuditLogRequest;
import com.revplay.analyticsservice.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {
    void logAction(Long userId, AuditLogRequest request, String ipAddress);

    Page<AuditLog> getUserLogs(Long userId, Pageable pageable);

    Page<AuditLog> getAllLogs(Pageable pageable);
}
