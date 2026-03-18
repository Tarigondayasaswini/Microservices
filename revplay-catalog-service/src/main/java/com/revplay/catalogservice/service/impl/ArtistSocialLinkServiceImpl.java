package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.dto.request.ArtistSocialLinkCreateRequest;
import com.revplay.catalogservice.dto.request.ArtistSocialLinkUpdateRequest;
import com.revplay.catalogservice.dto.response.ArtistSocialLinkResponse;
import com.revplay.catalogservice.entity.Artist;
import com.revplay.catalogservice.entity.ArtistSocialLink;
import com.revplay.catalogservice.enums.UserRole;
import com.revplay.catalogservice.exception.ConflictException;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.mapper.ArtistSocialLinkMapper;
import com.revplay.catalogservice.repository.ArtistRepository;
import com.revplay.catalogservice.repository.ArtistSocialLinkRepository;
import com.revplay.catalogservice.service.ArtistSocialLinkService;
import com.revplay.catalogservice.util.AccessValidator;
import com.revplay.catalogservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistSocialLinkServiceImpl implements ArtistSocialLinkService {
    private final ArtistSocialLinkRepository repository;
    private final ArtistRepository artistRepository;
    private final ArtistSocialLinkMapper mapper;
    private final SecurityUtil securityUtil;
    private final AccessValidator accessValidator;

    @Override
    @Transactional
    public ArtistSocialLinkResponse create(Long artistId, ArtistSocialLinkCreateRequest request) {
        log.info("Creating social link for artistId={}", artistId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        ensureOwnership(artistId);
        validateUniquePlatform(artistId, request.getPlatform(), null);
        ArtistSocialLink link = mapper.toEntity(request, artistId);
        return mapper.toResponse(repository.save(link));
    }

    @Override
    public List<ArtistSocialLinkResponse> list(Long artistId) {
        log.info("Listing social links for artistId={}", artistId);
        return repository.findByArtistId(artistId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ArtistSocialLinkResponse update(Long artistId, Long linkId, ArtistSocialLinkUpdateRequest request) {
        log.info("Updating social linkId={} for artistId={}", linkId, artistId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        ensureOwnership(artistId);
        
        ArtistSocialLink link = repository.findById(linkId)
                .orElseThrow(() -> new ResourceNotFoundException("SocialLink", linkId));
        
        if (!link.getArtistId().equals(artistId)) {
             throw new ResourceNotFoundException("SocialLink", linkId);
        }
        
        validateUniquePlatform(artistId, request.getPlatform(), linkId);
        mapper.updateEntity(link, request);
        return mapper.toResponse(repository.save(link));
    }

    @Override
    @Transactional
    public void delete(Long artistId, Long linkId) {
        log.info("Deleting social linkId={} for artistId={}", linkId, artistId);
        accessValidator.requireArtistOrAdmin(securityUtil.getUserRole());
        ensureOwnership(artistId);
        
        ArtistSocialLink link = repository.findById(linkId)
                .orElseThrow(() -> new ResourceNotFoundException("SocialLink", linkId));
        
        if (!link.getArtistId().equals(artistId)) {
            throw new ResourceNotFoundException("SocialLink", linkId);
        }
        repository.delete(link);
    }

    private void ensureOwnership(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));
        
        String role = securityUtil.getUserRole();
        if (!UserRole.ADMIN.name().equalsIgnoreCase(role) && !artist.getUserId().equals(securityUtil.getUserId())) {
            throw new ResourceNotFoundException("Artist", artistId);
        }
    }

    private void validateUniquePlatform(Long artistId, com.revplay.catalogservice.enums.SocialPlatform platform, Long linkId) {
        boolean exists = linkId == null
                ? repository.existsByArtistIdAndPlatform(artistId, platform)
                : repository.existsByArtistIdAndPlatformAndLinkIdNot(artistId, platform, linkId);
        if (exists) {
            throw new ConflictException("SocialLink", "platform", platform.name());
        }
    }

    // --- Alias methods used by ArtistSocialLinkController ---

    @Override
    public java.util.List<ArtistSocialLinkResponse> getLinksByArtistId(Long artistId) {
        return list(artistId);
    }

    @Override
    @Transactional
    public ArtistSocialLinkResponse upsertLink(Long artistId, com.revplay.catalogservice.dto.request.ArtistSocialLinkRequest request) {
        // Convert generic ArtistSocialLinkRequest to the appropriate create/update request
        ArtistSocialLink existing = request.getId() == null ? null
                : repository.findById(request.getId()).filter(l -> l.getArtistId().equals(artistId)).orElse(null);

        if (existing == null) {
            ArtistSocialLinkCreateRequest createReq = new ArtistSocialLinkCreateRequest();
            // Use reflection-free approach: map directly
            ArtistSocialLink link = new ArtistSocialLink();
            link.setArtistId(artistId);
            // Map platform string to enum
            try {
                link.setPlatform(com.revplay.catalogservice.enums.SocialPlatform.valueOf(request.getPlatform().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new com.revplay.catalogservice.exception.BadRequestException("Invalid platform: " + request.getPlatform());
            }
            link.setUrl(request.getUrl());
            return mapper.toResponse(repository.save(link));
        } else {
            try {
                existing.setPlatform(com.revplay.catalogservice.enums.SocialPlatform.valueOf(request.getPlatform().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new com.revplay.catalogservice.exception.BadRequestException("Invalid platform: " + request.getPlatform());
            }
            existing.setUrl(request.getUrl());
            return mapper.toResponse(repository.save(existing));
        }
    }

    @Override
    @Transactional
    public void deleteLink(Long artistId, Long linkId) {
        delete(artistId, linkId);
    }
}

