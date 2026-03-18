package com.revplay.playlistservice.audit.service.impl;

import com.revplay.playlistservice.audit.enums.AuditActionType;
import com.revplay.playlistservice.audit.enums.AuditEntityType;
import com.revplay.playlistservice.audit.service.AuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditLogServiceImpl implements AuditLogService {
    @Override
    public void logInternal(AuditActionType action, Long performedBy, AuditEntityType entityType, Long entityId, String description) {
        log.info("Audit Log [Internal]: Action={}, PerformedBy={}, EntityType={}, EntityId={}, Description={}",
                action, performedBy, entityType, entityId, description);
    }
}
