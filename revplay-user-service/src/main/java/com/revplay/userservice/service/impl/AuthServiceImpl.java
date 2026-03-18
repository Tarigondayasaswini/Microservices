package com.revplay.userservice.service.impl;

import com.revplay.userservice.dto.request.*;
import com.revplay.userservice.dto.response.AuthTokenResponse;
import com.revplay.userservice.dto.response.SimpleMessageResponse;
import com.revplay.userservice.dto.response.UserResponse;
import com.revplay.userservice.entity.PasswordResetToken;
import com.revplay.userservice.entity.User;
import com.revplay.userservice.entity.UserProfile;
import com.revplay.userservice.enums.UserRole;
import com.revplay.userservice.exception.AuthConflictException;
import com.revplay.userservice.exception.AuthNotFoundException;
import com.revplay.userservice.exception.AuthUnauthorizedException;
import com.revplay.userservice.exception.AuthValidationException;
import com.revplay.userservice.repository.PasswordResetTokenRepository;
import com.revplay.userservice.repository.UserProfileRepository;
import com.revplay.userservice.repository.UserRepository;
import com.revplay.userservice.security.AuthenticatedUserPrincipal;
import com.revplay.userservice.security.JwtProperties;
import com.revplay.userservice.security.service.JwtService;
import com.revplay.userservice.security.service.TokenRevocationService;
import com.revplay.userservice.service.AuthService;
import com.revplay.userservice.service.EmailService;
import com.revplay.userservice.util.OtpGeneratorUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final TokenRevocationService tokenRevocationService;
    private final EmailService emailService;

    public AuthServiceImpl(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            JwtProperties jwtProperties,
            TokenRevocationService tokenRevocationService,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.tokenRevocationService = tokenRevocationService;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public AuthTokenResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String username = request.username().trim();

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new AuthConflictException("Email already exists");
        }
        if (userRepository.findByUsernameIgnoreCase(username).isPresent()) {
            throw new AuthConflictException("Username already exists");
        }

        Instant now = Instant.now();
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.from(request.role()));
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        String otp = OtpGeneratorUtil.generateOtp();
        user.setEmailOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

        User savedUser = userRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUserId(savedUser.getUserId());
        profile.setFullName(request.fullName().trim());
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);
        userProfileRepository.save(profile);

        emailService.sendEmail(email, "RevPlay Verification", "Your OTP is: " + otp);

        return buildTokenResponse(savedUser);
    }

    @Override
    public AuthTokenResponse login(LoginRequest request) {
        User user = resolveUserByUsernameOrEmail(request.usernameOrEmail())
                .orElseThrow(() -> new AuthUnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthUnauthorizedException("Invalid credentials");
        }
        if (!user.getIsActive()) {
            throw new AuthUnauthorizedException("Account deactivated");
        }

        return buildTokenResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public AuthTokenResponse refreshToken(RefreshTokenRequest request) {
        if (tokenRevocationService.isRevoked(request.refreshToken())) {
            throw new AuthUnauthorizedException("Refresh token revoked");
        }
        if (!jwtService.isRefreshToken(request.refreshToken())) {
            throw new AuthUnauthorizedException("Invalid refresh token");
        }

        AuthenticatedUserPrincipal principal = jwtService.toPrincipal(request.refreshToken());
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new AuthUnauthorizedException("User not found"));

        return buildTokenResponse(user);
    }

    @Override
    public SimpleMessageResponse logout(String bearerToken) {
        if (bearerToken != null && !bearerToken.isBlank()) {
            tokenRevocationService.revoke(bearerToken, jwtService.getExpiry(bearerToken));
        }
        return new SimpleMessageResponse("Logged out successfully");
    }

    @Transactional
    @Override
    public SimpleMessageResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new AuthNotFoundException("User not found"));

        passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken tokenEntity = new PasswordResetToken();
        tokenEntity.setUser(user);
        tokenEntity.setToken(UUID.randomUUID().toString());
        tokenEntity.setExpiryDate(Instant.now().plusSeconds(1800));
        tokenEntity.setCreatedAt(Instant.now());
        passwordResetTokenRepository.save(tokenEntity);

        emailService.sendEmail(user.getEmail(), "Password Reset", "Token: " + tokenEntity.getToken());

        return new SimpleMessageResponse("Password reset email sent");
    }

    @Transactional
    @Override
    public SimpleMessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken tokenEntity = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new AuthValidationException("Invalid reset token"));

        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(tokenEntity);
            throw new AuthValidationException("Reset token expired");
        }

        User user = tokenEntity.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        passwordResetTokenRepository.delete(tokenEntity);

        return new SimpleMessageResponse("Password reset successful");
    }

    @Transactional
    @Override
    public SimpleMessageResponse verifyEmailOtp(String email, String otp) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthNotFoundException("User not found"));

        if (user.getEmailVerified()) {
            return new SimpleMessageResponse("Already verified");
        }

        // Check if OTP exists at all
        if (user.getEmailOtp() == null || user.getEmailOtp().isBlank()) {
            throw new AuthValidationException("No OTP found. Please request a new verification code.");
        }

        // Check OTP expiry BEFORE comparing value
        if (user.getOtpExpiryTime() != null && user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            user.setEmailOtp(null);
            user.setOtpExpiryTime(null);
            userRepository.save(user);
            throw new AuthValidationException("OTP expired. Please request a new verification code.");
        }

        // Compare OTP value
        if (!otp.trim().equals(user.getEmailOtp().trim())) {
            throw new AuthValidationException("Invalid OTP");
        }

        user.setEmailVerified(true);
        user.setEmailOtp(null);
        user.setOtpExpiryTime(null);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return new SimpleMessageResponse("Email verified successfully");
    }

    @Transactional
    @Override
    public SimpleMessageResponse resendEmailOtp(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthNotFoundException("User not found"));

        if (user.getEmailVerified()) {
            return new SimpleMessageResponse("Already verified");
        }

        String otp = OtpGeneratorUtil.generateOtp();
        user.setEmailOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        emailService.sendEmail(user.getEmail(), "RevPlay Verification", "Your new OTP is: " + otp);

        return new SimpleMessageResponse("OTP sent");
    }

    private AuthTokenResponse buildTokenResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        UserResponse userResponse = new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());

        return new AuthTokenResponse(
                "Bearer",
                accessToken,
                jwtProperties.getAccessTokenExpirationSeconds(),
                refreshToken,
                jwtProperties.getRefreshTokenExpirationSeconds(),
                userResponse);
    }

    private Optional<User> resolveUserByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return userRepository.findByEmailIgnoreCase(usernameOrEmail);
        }
        return userRepository.findByUsernameIgnoreCase(usernameOrEmail);
    }

    @Transactional
    @Override
    public SimpleMessageResponse changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new AuthValidationException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return new SimpleMessageResponse("Password changed successfully");
    }
}
