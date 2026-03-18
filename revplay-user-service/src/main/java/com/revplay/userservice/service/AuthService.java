package com.revplay.userservice.service;

import com.revplay.userservice.dto.request.ChangePasswordRequest;
import com.revplay.userservice.dto.request.ForgotPasswordRequest;
import com.revplay.userservice.dto.request.LoginRequest;
import com.revplay.userservice.dto.request.RefreshTokenRequest;
import com.revplay.userservice.dto.request.RegisterRequest;
import com.revplay.userservice.dto.request.ResetPasswordRequest;
import com.revplay.userservice.dto.response.AuthTokenResponse;
import com.revplay.userservice.dto.response.SimpleMessageResponse;

public interface AuthService {
    AuthTokenResponse register(RegisterRequest request);

    AuthTokenResponse login(LoginRequest request);

    AuthTokenResponse refreshToken(RefreshTokenRequest request);

    SimpleMessageResponse logout(String bearerToken);

    SimpleMessageResponse forgotPassword(ForgotPasswordRequest request);

    SimpleMessageResponse resetPassword(ResetPasswordRequest request);

    SimpleMessageResponse verifyEmailOtp(String email, String otp);

    SimpleMessageResponse resendEmailOtp(String email);

    SimpleMessageResponse changePassword(Long userId, ChangePasswordRequest request);
}
