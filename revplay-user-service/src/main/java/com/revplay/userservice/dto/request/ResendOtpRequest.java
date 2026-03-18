package com.revplay.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResendOtpRequest(
        @NotBlank(message = "Email is required") String email) {
}
