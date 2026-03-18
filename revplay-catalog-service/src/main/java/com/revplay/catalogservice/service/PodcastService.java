package com.revplay.catalogservice.service;

import com.revplay.catalogservice.dto.request.PodcastCreateRequest;
import com.revplay.catalogservice.dto.request.PodcastUpdateRequest;
import com.revplay.catalogservice.dto.response.PodcastResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PodcastService {

    PodcastResponse create(PodcastCreateRequest request);

    PodcastResponse update(Long podcastId, PodcastUpdateRequest request);

    PodcastResponse get(Long podcastId);

    void delete(Long podcastId);

    Page<PodcastResponse> listByArtist(Long artistId, Pageable pageable);

    Page<PodcastResponse> listRecommended(Pageable pageable);
}
