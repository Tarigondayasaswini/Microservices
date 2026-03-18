package com.revplay.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailOtpRequest(
        @NotBlank(message = "Email is required") String email,

        @NotBlank(message = "OTP is required") String otp) {
}
