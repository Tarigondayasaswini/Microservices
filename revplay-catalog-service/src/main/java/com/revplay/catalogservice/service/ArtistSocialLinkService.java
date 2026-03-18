package com.revplay.catalogservice.service;

import java.util.List;

import com.revplay.catalogservice.dto.request.ArtistSocialLinkCreateRequest;
import com.revplay.catalogservice.dto.request.ArtistSocialLinkRequest;
import com.revplay.catalogservice.dto.request.ArtistSocialLinkUpdateRequest;
import com.revplay.catalogservice.dto.response.ArtistSocialLinkResponse;

public interface ArtistSocialLinkService {

    ArtistSocialLinkResponse create(Long artistId, ArtistSocialLinkCreateRequest request);

    List<ArtistSocialLinkResponse> list(Long artistId);

    ArtistSocialLinkResponse update(Long artistId, Long linkId, ArtistSocialLinkUpdateRequest request);

    void delete(Long artistId, Long linkId);

    // Methods used by ArtistSocialLinkController
    List<ArtistSocialLinkResponse> getLinksByArtistId(Long artistId);

    ArtistSocialLinkResponse upsertLink(Long artistId, ArtistSocialLinkRequest request);

    void deleteLink(Long artistId, Long linkId);
}

