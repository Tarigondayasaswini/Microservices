package com.revplay.analyticsservice.service.impl;

import com.revplay.analyticsservice.client.UserServiceClient;
import com.revplay.analyticsservice.entity.UserSubscription;
import com.revplay.analyticsservice.exception.InvalidStateTransitionException;
import com.revplay.analyticsservice.exception.ResourceNotFoundException;
import com.revplay.analyticsservice.repository.UserSubscriptionRepository;
import com.revplay.analyticsservice.service.SubscriptionService;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserServiceClient userServiceClient;

    public SubscriptionServiceImpl(UserSubscriptionRepository userSubscriptionRepository,
            UserServiceClient userServiceClient) {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    @Override
    public void createSubscription(Long userId, String planType) {
        // Verify user exists via Feign
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found");
        }

        Optional<UserSubscription> existingOpt = userSubscriptionRepository.findByUserId(userId);
        UserSubscription sub;

        if (existingOpt.isPresent()) {
            sub = existingOpt.get();
            if ("ACTIVE".equals(sub.getStatus()) && sub.getExpiresAt().isAfter(Instant.now())) {
                throw new InvalidStateTransitionException("User already has an active subscription");
            }
            sub.setStatus("ACTIVE");
            sub.setPlanType(planType);
            sub.setStartedAt(Instant.now());
            sub.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        } else {
            sub = new UserSubscription();
            sub.setUserId(userId);
            sub.setPlanType(planType);
            sub.setStatus("ACTIVE");
            sub.setStartedAt(Instant.now());
            sub.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS)); // Default 30 days
        }

        userSubscriptionRepository.save(sub);
    }

    @Transactional
    @Override
    public void cancelSubscription(Long userId) {
        UserSubscription sub = userSubscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if ("CANCELLED".equals(sub.getStatus())) {
            throw new InvalidStateTransitionException("Subscription already cancelled");
        }

        sub.setStatus("CANCELLED");
        userSubscriptionRepository.save(sub);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasActivePremium(Long userId) {
        Optional<UserSubscription> opt = userSubscriptionRepository.findByUserIdAndStatus(userId, "ACTIVE");
        if (opt.isPresent()) {
            UserSubscription sub = opt.get();
            if (sub.getExpiresAt().isAfter(Instant.now())) {
                return true;
            }
        }
        return false;
    }
}
