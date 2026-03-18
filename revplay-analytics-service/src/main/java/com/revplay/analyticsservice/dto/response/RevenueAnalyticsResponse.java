package com.revplay.analyticsservice.dto.response;

public record RevenueAnalyticsResponse(
        double monthlyRevenue,
        double yearlyRevenue,
        double totalRevenue
) {
}

