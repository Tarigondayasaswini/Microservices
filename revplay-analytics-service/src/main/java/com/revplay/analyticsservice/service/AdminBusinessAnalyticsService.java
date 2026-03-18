package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.dto.response.BusinessOverviewResponse;
import com.revplay.analyticsservice.dto.response.ConversionRateResponse;
import com.revplay.analyticsservice.dto.response.RevenueAnalyticsResponse;
import com.revplay.analyticsservice.dto.response.TopDownloadResponse;
import com.revplay.analyticsservice.dto.response.TopMixResponse;
import java.util.List;

public interface AdminBusinessAnalyticsService {

    BusinessOverviewResponse getBusinessOverview();

    RevenueAnalyticsResponse getRevenueAnalytics();

    List<TopDownloadResponse> getTopDownloadedSongs(int limit);

    List<TopMixResponse> getTopMixes();

    ConversionRateResponse getPremiumConversionRate();
}

