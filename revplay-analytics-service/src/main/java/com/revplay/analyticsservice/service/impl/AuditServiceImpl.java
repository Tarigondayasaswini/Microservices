package com.revplay.analyticsservice.service.impl;

import com.revplay.analyticsservice.dto.AuditLogRequest;
import com.revplay.analyticsservice.entity.AuditLog;
import com.revplay.analyticsservice.repository.AuditLogRepository;
import com.revplay.analyticsservice.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    @Override
    public void logAction(Long userId, AuditLogRequest request, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setActionType(request.getAction());
        log.setEntityType(request.getEntityType());
        log.setEntityId(request.getEntityId());
        log.setDetails(request.getDetails());

        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AuditLog> getUserLogs(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable);
    }
}
