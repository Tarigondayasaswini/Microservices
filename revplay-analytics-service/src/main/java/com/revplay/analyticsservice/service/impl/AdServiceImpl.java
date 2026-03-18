package com.revplay.analyticsservice.service.impl;

import com.revplay.analyticsservice.dto.AdCampaignDto;
import com.revplay.analyticsservice.dto.CreateAdCampaignRequest;
import com.revplay.analyticsservice.entity.AdCampaign;
import com.revplay.analyticsservice.exception.ResourceNotFoundException;
import com.revplay.analyticsservice.repository.AdCampaignRepository;
import com.revplay.analyticsservice.service.AdService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdServiceImpl implements AdService {

    private final AdCampaignRepository adCampaignRepository;

    public AdServiceImpl(AdCampaignRepository adCampaignRepository) {
        this.adCampaignRepository = adCampaignRepository;
    }

    @Transactional
    @Override
    public AdCampaignDto createCampaign(CreateAdCampaignRequest request) {
        AdCampaign ad = new AdCampaign();
        ad.setTitle(request.getTitle());
        ad.setSponsorName(request.getSponsorName());
        ad.setAudioUrl(request.getAudioUrl());
        ad.setDurationSeconds(request.getDurationSeconds());
        ad.setTargetUrl(request.getTargetUrl());

        AdCampaign saved = adCampaignRepository.save(ad);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public AdCampaignDto getRandomActiveAd() {
        return adCampaignRepository.findRandomActiveAd()
                .map(this::mapToDto)
                .orElse(null); // Or throw an exception, depending on client expectations
    }

    @Transactional
    @Override
    public void recordImpression(Long campaignId) {
        AdCampaign ad = adCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Ad Campaign not found"));
        ad.setImpressionsCount(ad.getImpressionsCount() + 1);
        adCampaignRepository.save(ad);
    }

    @Transactional
    @Override
    public void recordClick(Long campaignId) {
        AdCampaign ad = adCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Ad Campaign not found"));
        ad.setClicksCount(ad.getClicksCount() + 1);
        adCampaignRepository.save(ad);
    }

    @Transactional
    @Override
    public void deactivateCampaign(Long campaignId) {
        AdCampaign ad = adCampaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Ad Campaign not found"));
        ad.setIsActive(false);
        adCampaignRepository.save(ad);
    }

    @Transactional(readOnly = true)
    @Override
    public com.revplay.analyticsservice.entity.Ad getNextAd(Long userId, Long songId) {
        // Simple logic for now: get a random ad from the entity table if it exists
        // Or just return null/mock
        return null; 
    }

    private AdCampaignDto mapToDto(AdCampaign ad) {
        return AdCampaignDto.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .sponsorName(ad.getSponsorName())
                .audioUrl(ad.getAudioUrl())
                .durationSeconds(ad.getDurationSeconds())
                .targetUrl(ad.getTargetUrl())
                .impressionsCount(ad.getImpressionsCount())
                .clicksCount(ad.getClicksCount())
                .isActive(ad.getIsActive())
                .createdAt(ad.getCreatedAt())
                .build();
    }
}
