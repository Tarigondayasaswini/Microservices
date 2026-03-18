package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.dto.request.ArtistCreateRequest;
import com.revplay.catalogservice.dto.request.ArtistUpdateRequest;
import com.revplay.catalogservice.dto.response.ArtistResponse;
import com.revplay.catalogservice.dto.response.PodcastResponse;
import com.revplay.catalogservice.dto.response.SongResponse;
import com.revplay.catalogservice.entity.Artist;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.mapper.ArtistMapper;
import com.revplay.catalogservice.mapper.PodcastMapper;
import com.revplay.catalogservice.mapper.SongMapper;
import com.revplay.catalogservice.repository.AlbumRepository;
import com.revplay.catalogservice.repository.ArtistRepository;
import com.revplay.catalogservice.repository.PodcastRepository;
import com.revplay.catalogservice.repository.SongRepository;
import com.revplay.catalogservice.dto.response.ArtistSummaryResponse;
import com.revplay.catalogservice.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final PodcastRepository podcastRepository;
    private final PodcastMapper podcastMapper;
    private final AlbumRepository albumRepository;

    @Transactional(readOnly = true)
    @Override
    public ArtistResponse getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", id));
        return artistMapper.toResponse(artist);
    }

    @Transactional(readOnly = true)
    @Override
    public ArtistResponse getArtistByUserId(Long userId) {
        Artist artist = artistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist profile", userId));
        return artistMapper.toResponse(artist);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ArtistResponse> searchArtists(String query, Pageable pageable) {
        return artistRepository.findByDisplayNameContainingIgnoreCase(query, pageable)
                .map(artistMapper::toResponse);
    }

    @Transactional
    @Override
    public ArtistResponse createArtist(Long userId, ArtistCreateRequest request) {
        Artist artist = artistMapper.toEntity(request, userId);
        Artist saved = artistRepository.save(artist);
        return artistMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public ArtistResponse updateArtist(Long artistId, ArtistUpdateRequest request) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));
        
        artistMapper.updateEntity(artist, request);
        Artist saved = artistRepository.save(artist);
        return artistMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public ArtistSummaryResponse getArtistSummary(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));

        ArtistSummaryResponse summary = new ArtistSummaryResponse();
        summary.setArtistId(artistId);
        summary.setSongCount(songRepository.countByArtistIdAndIsActiveTrue(artistId));
        summary.setAlbumCount(albumRepository.countByArtistId(artistId));
        summary.setPodcastCount(podcastRepository.countByArtistIdAndIsActiveTrue(artistId));
        return summary;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<SongResponse> getArtistSongs(Long artistId, Pageable pageable) {
        return songRepository.findByArtistIdAndIsActiveTrue(artistId, pageable)
                .map(songMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PodcastResponse> getArtistPodcasts(Long artistId, Pageable pageable) {
        return podcastRepository.findByArtistIdAndIsActiveTrue(artistId, pageable)
                .map(podcastMapper::toResponse);
    }
}
