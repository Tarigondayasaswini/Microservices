package com.revplay.analyticsservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class AdCampaignDto {
    private Long id;
    private String title;
    private String sponsorName;
    private String audioUrl;
    private Integer durationSeconds;
    private String targetUrl;
    private Long impressionsCount;
    private Long clicksCount;
    private Boolean isActive;
    private Instant createdAt;
}
