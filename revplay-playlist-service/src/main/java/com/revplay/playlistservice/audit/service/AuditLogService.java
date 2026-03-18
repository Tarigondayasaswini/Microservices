package com.revplay.playlistservice.audit.service;

import com.revplay.playlistservice.audit.enums.AuditActionType;
import com.revplay.playlistservice.audit.enums.AuditEntityType;

public interface AuditLogService {
    void logInternal(AuditActionType action, Long performedBy, AuditEntityType entityType, Long entityId, String description);
}
