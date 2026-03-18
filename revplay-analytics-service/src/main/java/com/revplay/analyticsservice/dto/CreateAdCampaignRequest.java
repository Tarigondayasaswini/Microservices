package com.revplay.analyticsservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdCampaignRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Sponsor name is required")
    private String sponsorName;

    @NotBlank(message = "Audio URL is required")
    private String audioUrl;

    @Min(value = 1, message = "Duration must be positive")
    private Integer durationSeconds;

    private String targetUrl;
}
