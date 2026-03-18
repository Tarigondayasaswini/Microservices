package com.revplay.analyticsservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuditLogRequest {
    @NotBlank(message = "Action is required")
    private String action;

    private String entityType;
    private Long entityId;
    private String details;
}
