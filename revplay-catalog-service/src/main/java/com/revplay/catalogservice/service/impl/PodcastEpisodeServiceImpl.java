package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.repository.ArtistRepository;
import com.revplay.catalogservice.audit.PodcastEpisodeDeletedEvent;
import com.revplay.catalogservice.dto.request.PodcastEpisodeCreateRequest;
import com.revplay.catalogservice.dto.request.PodcastEpisodeUpdateRequest;
import com.revplay.catalogservice.dto.response.PodcastEpisodeResponse;
import com.revplay.catalogservice.entity.Podcast;
import com.revplay.catalogservice.entity.PodcastEpisode;
import com.revplay.catalogservice.mapper.PodcastEpisodeMapper;
import com.revplay.catalogservice.repository.PodcastEpisodeRepository;
import com.revplay.catalogservice.repository.PodcastRepository;
import com.revplay.catalogservice.service.ContentValidationService;
import com.revplay.catalogservice.service.PodcastEpisodeService;
import com.revplay.catalogservice.util.AccessValidator;
import com.revplay.catalogservice.util.AudioMetadataService;
import com.revplay.catalogservice.util.FileStorageService;
import com.revplay.catalogservice.util.SecurityUtil;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PodcastEpisodeServiceImpl implements PodcastEpisodeService {
    private final PodcastEpisodeRepository episodeRepository;
    private final PodcastRepository podcastRepository;
    private final PodcastEpisodeMapper mapper;
    private final FileStorageService fileStorageService;
    private final SecurityUtil securityUtil;
    private final AccessValidator accessValidator;
    private final ContentValidationService contentValidationService;
    private final AudioMetadataService audioMetadataService;
    private final ArtistRepository artistRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PodcastEpisodeResponse create(Long podcastId, PodcastEpisodeCreateRequest request, MultipartFile audioFile) {
        log.info("Creating episode for podcastId={}", podcastId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        Podcast podcast = getOwnedPodcast(podcastId);
        Integer durationSeconds = audioMetadataService.resolveDurationSeconds(audioFile, request.getDurationSeconds());
        contentValidationService.validatePodcastEpisodeDuration(durationSeconds);
        String fileName = fileStorageService.storePodcast(audioFile);
        String audioUrl = "/api/v1/files/podcasts/" + fileName;
        PodcastEpisode episode = mapper.toEntity(request, podcast.getPodcastId(), audioUrl);
        episode.setDurationSeconds(durationSeconds);
        return mapper.toResponse(episodeRepository.save(episode));
    }

    @Override
    @Transactional
    public PodcastEpisodeResponse update(Long podcastId, Long episodeId, PodcastEpisodeUpdateRequest request) {
        log.info("Updating episodeId={}", episodeId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        PodcastEpisode episode = getOwnedEpisode(podcastId, episodeId);
        contentValidationService.validatePodcastEpisodeDuration(request.getDurationSeconds());
        mapper.updateEntity(episode, request);
        return mapper.toResponse(episodeRepository.save(episode));
    }

    @Override
    public PodcastEpisodeResponse get(Long podcastId, Long episodeId) {
        log.info("Fetching episodeId={}", episodeId);
        PodcastEpisode episode = getEpisodeByPodcast(podcastId, episodeId);
        return mapper.toResponse(episode);
    }

    @Override
    @Transactional
    public void delete(Long podcastId, Long episodeId) {
        log.info("Deleting episodeId={}", episodeId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        PodcastEpisode episode = getOwnedEpisode(podcastId, episodeId);
        String previousFile = extractFileName(episode.getAudioUrl());
        episodeRepository.delete(episode);
        if (previousFile != null) {
            fileStorageService.deletePodcastFile(previousFile);
        }
        eventPublisher.publishEvent(new PodcastEpisodeDeletedEvent(episode.getEpisodeId()));
    }

    @Override
    public Page<PodcastEpisodeResponse> listByPodcast(Long podcastId, Pageable pageable) {
        log.info("Listing episodes for podcastId={} page={}", podcastId, pageable.getPageNumber());
        return episodeRepository.findByPodcastId(podcastId, pageable)
            .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public PodcastEpisodeResponse replaceAudio(Long podcastId, Long episodeId, MultipartFile audioFile) {
        log.info("Replacing audio for episodeId={}", episodeId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        PodcastEpisode episode = getOwnedEpisode(podcastId, episodeId);
        String previousFile = extractFileName(episode.getAudioUrl());
        Integer durationSeconds = audioMetadataService.resolveDurationSeconds(audioFile, episode.getDurationSeconds());
        contentValidationService.validatePodcastEpisodeDuration(durationSeconds);
        String fileName = fileStorageService.storePodcast(audioFile);
        episode.setAudioUrl("/api/v1/files/podcasts/" + fileName);
        episode.setDurationSeconds(durationSeconds);
        PodcastEpisode saved = episodeRepository.save(episode);
        if (previousFile != null) {
            fileStorageService.deletePodcastFile(previousFile);
        }
        return mapper.toResponse(saved);
    }

    private PodcastEpisode getOwnedEpisode(Long podcastId, Long episodeId) {
        PodcastEpisode episode = getEpisodeByPodcast(podcastId, episodeId);
        getOwnedPodcast(podcastId);
        return episode;
    }

    private PodcastEpisode getEpisodeByPodcast(Long podcastId, Long episodeId) {
        PodcastEpisode episode = episodeRepository.findById(episodeId)
            .orElseThrow(() -> new ResourceNotFoundException("Episode", episodeId));
        if (!episode.getPodcastId().equals(podcastId)) {
            throw new ResourceNotFoundException("Episode", episodeId);
        }
        return episode;
    }

    private Podcast getOwnedPodcast(Long podcastId) {
        Podcast podcast = podcastRepository.findById(podcastId)
            .orElseThrow(() -> new ResourceNotFoundException("Podcast", podcastId));
        String role = securityUtil.getUserRole();
        if (!UserRole.ADMIN.name().equalsIgnoreCase(role)) {
            artistRepository.findById(podcast.getArtistId())
                .filter(a -> a.getUserId().equals(securityUtil.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Podcast", podcastId));
        }
        return podcast;
    }

    private String extractFileName(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return null;
        }
        int idx = fileUrl.lastIndexOf('/');
        if (idx < 0 || idx == fileUrl.length() - 1) {
            return null;
        }
        return fileUrl.substring(idx + 1);
    }
}
