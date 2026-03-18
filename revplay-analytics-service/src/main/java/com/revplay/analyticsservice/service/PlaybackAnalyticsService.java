package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.dto.response.DashboardMetricsResponse;
import com.revplay.analyticsservice.dto.response.TopArtistResponse;
import com.revplay.analyticsservice.dto.response.TrendingContentResponse;
import com.revplay.analyticsservice.dto.response.UserListeningStatsResponse;
import com.revplay.analyticsservice.enums.TimePeriod;

import java.util.List;

public interface PlaybackAnalyticsService {

    List<TrendingContentResponse> trending(String type, TimePeriod period, int limit);

    List<TopArtistResponse> topArtists(int limit);

    List<TrendingContentResponse> topContent(String type, int limit);

    UserListeningStatsResponse userStats(Long userId);

    DashboardMetricsResponse dashboardMetrics();
}



