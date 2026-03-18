package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.dto.AdCampaignDto;
import com.revplay.analyticsservice.dto.CreateAdCampaignRequest;

public interface AdService {
    AdCampaignDto createCampaign(CreateAdCampaignRequest request);

    AdCampaignDto getRandomActiveAd();

    void recordImpression(Long campaignId);

    void recordClick(Long campaignId);

    void deactivateCampaign(Long campaignId);

    com.revplay.analyticsservice.entity.Ad getNextAd(Long userId, Long songId);
}
