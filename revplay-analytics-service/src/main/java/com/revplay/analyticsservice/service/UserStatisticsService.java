package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.dto.response.UserStatisticsResponse;

public interface UserStatisticsService {

    UserStatisticsResponse getByUserId(Long userId);

    UserStatisticsResponse refreshAndGet(Long userId);
}



