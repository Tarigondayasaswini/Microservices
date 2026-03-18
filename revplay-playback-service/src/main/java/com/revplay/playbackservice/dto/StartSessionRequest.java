package com.revplay.playbackservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StartSessionRequest {
    @NotBlank(message = "Device info is required")
    private String deviceInfo;
}
