package com.revplay.analyticsservice.dto.response;

public record TrendingContentResponse(
        String type,
        Long contentId,
        String title,
        Long playCount
) {
}





