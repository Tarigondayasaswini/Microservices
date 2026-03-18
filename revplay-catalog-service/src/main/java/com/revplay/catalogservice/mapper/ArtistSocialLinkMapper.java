package com.revplay.catalogservice.mapper;

import com.revplay.catalogservice.dto.request.ArtistSocialLinkCreateRequest;
import com.revplay.catalogservice.dto.request.ArtistSocialLinkUpdateRequest;
import com.revplay.catalogservice.dto.response.ArtistSocialLinkResponse;
import com.revplay.catalogservice.entity.ArtistSocialLink;
import org.springframework.stereotype.Component;

@Component
public class ArtistSocialLinkMapper {
    public ArtistSocialLink toEntity(ArtistSocialLinkCreateRequest request, Long artistId) {
        ArtistSocialLink link = new ArtistSocialLink();
        link.setArtistId(artistId);
        link.setPlatform(request.getPlatform());
        link.setUrl(request.getUrl());
        return link;
    }

    public void updateEntity(ArtistSocialLink link, ArtistSocialLinkUpdateRequest request) {
        link.setPlatform(request.getPlatform());
        link.setUrl(request.getUrl());
    }

    public ArtistSocialLinkResponse toResponse(ArtistSocialLink link) {
        ArtistSocialLinkResponse response = new ArtistSocialLinkResponse();
        response.setLinkId(link.getLinkId());
        response.setArtistId(link.getArtistId());
        response.setPlatform(link.getPlatform());
        response.setUrl(link.getUrl());
        return response;
    }
}
