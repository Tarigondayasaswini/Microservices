package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.entity.Artist;
import com.revplay.catalogservice.enums.ArtistType;
import com.revplay.catalogservice.repository.ArtistRepository;
import com.revplay.catalogservice.audit.PodcastDeletedEvent;
import com.revplay.catalogservice.dto.request.PodcastCreateRequest;
import com.revplay.catalogservice.dto.request.PodcastUpdateRequest;
import com.revplay.catalogservice.dto.response.PodcastResponse;
import com.revplay.catalogservice.entity.Podcast;
import com.revplay.catalogservice.mapper.PodcastMapper;
import com.revplay.catalogservice.repository.PodcastRepository;
import com.revplay.catalogservice.service.PodcastService;
import com.revplay.catalogservice.util.AccessValidator;
import com.revplay.catalogservice.util.SecurityUtil;
import com.revplay.catalogservice.exception.BadRequestException;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PodcastServiceImpl implements PodcastService {
    private final PodcastRepository podcastRepository;
    private final ArtistRepository artistRepository;
    private final PodcastMapper mapper;
    private final SecurityUtil securityUtil;
    private final AccessValidator accessValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PodcastResponse create(PodcastCreateRequest request) {
        Long currentUserId = securityUtil.getUserId();
        log.info("Creating podcast for currentUserId={}", currentUserId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Artist artist = getOwnedArtistByUserId();
        if (artist.getArtistType() == ArtistType.MUSIC) {
            throw new BadRequestException("Artist not allowed to create podcasts");
        }
        Podcast podcast = mapper.toEntity(request, artist.getArtistId());
        return mapper.toResponse(podcastRepository.save(podcast));
    }

    @Override
    @Transactional
    public PodcastResponse update(Long podcastId, PodcastUpdateRequest request) {
        log.info("Updating podcastId={}", podcastId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Podcast podcast = getOwnedPodcast(podcastId);
        mapper.updateEntity(podcast, request);
        return mapper.toResponse(podcastRepository.save(podcast));
    }

    @Override
    public PodcastResponse get(Long podcastId) {
        log.info("Fetching podcastId={}", podcastId);
        Podcast podcast = podcastRepository.findById(podcastId)
            .orElseThrow(() -> new ResourceNotFoundException("Podcast", podcastId));
        if (Boolean.FALSE.equals(podcast.getIsActive())) {
            throw new ResourceNotFoundException("Podcast", podcastId);
        }
        return mapper.toResponse(podcast);
    }

    @Override
    @Transactional
    public void delete(Long podcastId) {
        log.info("Deleting podcastId={}", podcastId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Podcast podcast = getOwnedPodcast(podcastId);
        podcast.setIsActive(Boolean.FALSE);
        podcastRepository.save(podcast);
        eventPublisher.publishEvent(new PodcastDeletedEvent(podcast.getPodcastId()));
    }

    @Override
    public Page<PodcastResponse> listByArtist(Long artistId, Pageable pageable) {
        log.info("Listing podcasts for artistId={} page={}", artistId, pageable.getPageNumber());
        return podcastRepository.findByArtistIdAndIsActiveTrue(artistId, pageable)
            .map(mapper::toResponse);
    }

    @Override
    public Page<PodcastResponse> listRecommended(Pageable pageable) {
        log.info("Listing recommended podcasts page={}", pageable.getPageNumber());
        return podcastRepository.findRecommended(pageable)
            .map(mapper::toResponse);
    }

    private Podcast getOwnedPodcast(Long podcastId) {
        Podcast podcast = podcastRepository.findById(podcastId)
            .orElseThrow(() -> new ResourceNotFoundException("Podcast", podcastId));
        if (Boolean.FALSE.equals(podcast.getIsActive())) {
            throw new ResourceNotFoundException("Podcast", podcastId);
        }
        getOwnedArtist(podcast.getArtistId());
        return podcast;
    }

    private Artist getOwnedArtist(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
            .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));
        String role = securityUtil.getUserRole();
        if (!UserRole.ADMIN.name().equalsIgnoreCase(role) && !artist.getUserId().equals(securityUtil.getUserId())) {
            throw new ResourceNotFoundException("Artist", artistId);
        }
        return artist;
    }

    private Artist getOwnedArtistByUserId() {
        return artistRepository.findByUserId(securityUtil.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Artist profile", securityUtil.getUserId()));
    }
}
