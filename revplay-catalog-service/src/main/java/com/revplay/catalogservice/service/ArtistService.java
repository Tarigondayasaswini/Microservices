package com.revplay.catalogservice.service;

import com.revplay.catalogservice.dto.request.ArtistCreateRequest;
import com.revplay.catalogservice.dto.request.ArtistUpdateRequest;
import com.revplay.catalogservice.dto.response.ArtistResponse;
import com.revplay.catalogservice.dto.response.ArtistSummaryResponse;
import com.revplay.catalogservice.dto.response.SongResponse;
import com.revplay.catalogservice.dto.response.PodcastResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistService {
    ArtistResponse getArtistById(Long id);

    ArtistResponse getArtistByUserId(Long userId);

    Page<ArtistResponse> searchArtists(String query, Pageable pageable);

    ArtistResponse createArtist(Long userId, ArtistCreateRequest request);

    ArtistResponse updateArtist(Long artistId, ArtistUpdateRequest request);

    ArtistSummaryResponse getArtistSummary(Long artistId);

    Page<SongResponse> getArtistSongs(Long artistId, Pageable pageable);

    Page<PodcastResponse> getArtistPodcasts(Long artistId, Pageable pageable);
}

