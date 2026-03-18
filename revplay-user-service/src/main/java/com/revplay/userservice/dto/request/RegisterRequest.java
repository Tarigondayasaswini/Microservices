package com.revplay.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Size(max = 255) String email,

        @NotBlank(message = "Username is required") @Size(min = 3, max = 100) String username,

        @NotBlank(message = "Full name is required") @Size(max = 255) String fullName,

        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") String password,

        String role) {
}
