package com.revplay.userservice.controller;

import com.revplay.userservice.common.response.ApiResponse;
import com.revplay.userservice.dto.request.*;
import com.revplay.userservice.dto.response.AuthTokenResponse;
import com.revplay.userservice.dto.response.SimpleMessageResponse;
import com.revplay.userservice.security.AuthContextUtil;
import com.revplay.userservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.register(request), "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(request), "Token refreshed"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        return ResponseEntity.ok(ApiResponse.success(authService.logout(token), "Logout successful"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.forgotPassword(request), "Reset link sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authService.resetPassword(request), "Password reset successful"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> verifyEmail(
            @Valid @RequestBody VerifyEmailOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                authService.verifyEmailOtp(request.email(), request.otp()), "Email verified successfully"));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> resendVerification(
            @Valid @RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                authService.resendEmailOtp(request.email()), "OTP resent successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<SimpleMessageResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        Long currentUserId = AuthContextUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                authService.changePassword(currentUserId, request), "Password changed successfully"));
    }
}
