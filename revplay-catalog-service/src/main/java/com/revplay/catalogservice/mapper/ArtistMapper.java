package com.revplay.catalogservice.mapper;



import com.revplay.catalogservice.dto.request.ArtistCreateRequest;
import com.revplay.catalogservice.dto.request.ArtistUpdateRequest;
import com.revplay.catalogservice.dto.response.ArtistResponse;
import com.revplay.catalogservice.entity.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {
    public Artist toEntity(ArtistCreateRequest request, Long userId) {
        Artist artist = new Artist();
        artist.setUserId(userId);
        artist.setDisplayName(request.getDisplayName());
        artist.setBio(request.getBio());
        artist.setBannerImageUrl(request.getBannerImageUrl());
        artist.setArtistType(request.getArtistType());
        return artist;
    }

    public void updateEntity(Artist artist, ArtistUpdateRequest request) {
        artist.setDisplayName(request.getDisplayName());
        artist.setBio(request.getBio());
        artist.setBannerImageUrl(request.getBannerImageUrl());
        artist.setArtistType(request.getArtistType());
    }

    public ArtistResponse toResponse(Artist artist) {
        ArtistResponse response = new ArtistResponse();
        response.setArtistId(artist.getArtistId());
        response.setUserId(artist.getUserId());
        response.setDisplayName(artist.getDisplayName());
        response.setBio(artist.getBio());
        response.setBannerImageUrl(artist.getBannerImageUrl());
        response.setArtistType(artist.getArtistType());
        response.setVerified(artist.getVerified());
        if (artist.getCreatedAt() != null) {
            response.setCreatedAt(java.time.LocalDateTime.ofInstant(artist.getCreatedAt(), java.time.ZoneId.systemDefault()));
        }
        if (artist.getUpdatedAt() != null) {
            response.setUpdatedAt(java.time.LocalDateTime.ofInstant(artist.getUpdatedAt(), java.time.ZoneId.systemDefault()));
        }
        return response;
    }
}

