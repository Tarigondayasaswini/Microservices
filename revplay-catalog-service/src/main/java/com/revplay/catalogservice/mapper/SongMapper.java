package com.revplay.catalogservice.mapper;



import com.revplay.catalogservice.dto.request.SongCreateRequest;
import com.revplay.catalogservice.dto.request.SongUpdateRequest;
import com.revplay.catalogservice.dto.response.SongResponse;
import com.revplay.catalogservice.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {
    public Song toEntity(SongCreateRequest request, Long artistId, String fileUrl) {
        Song song = new Song();
        song.setArtistId(artistId);
        song.setAlbumId(request.getAlbumId());
        song.setTitle(request.getTitle());
        song.setDurationSeconds(request.getDurationSeconds());
        song.setReleaseDate(request.getReleaseDate());
        song.setFileUrl(fileUrl);
        song.setVisibility(request.getVisibility() != null ? request.getVisibility().name() : null);
        return song;
    }

    public void updateEntity(Song song, SongUpdateRequest request) {
        song.setTitle(request.getTitle());
        song.setDurationSeconds(request.getDurationSeconds());
        song.setAlbumId(request.getAlbumId());
        song.setReleaseDate(request.getReleaseDate());
    }

    public SongResponse toResponse(Song song) {
        SongResponse response = new SongResponse();
        response.setSongId(song.getSongId());
        response.setArtistId(song.getArtistId());
        response.setAlbumId(song.getAlbumId());
        response.setTitle(song.getTitle());
        response.setDurationSeconds(song.getDurationSeconds());
        response.setFileUrl(song.getFileUrl());
        if (song.getVisibility() != null) {
            try {
                response.setVisibility(com.revplay.catalogservice.enums.ContentVisibility.valueOf(song.getVisibility()));
            } catch (Exception e) {}
        }
        response.setReleaseDate(song.getReleaseDate());
        response.setIsActive(song.getIsActive());
        if (song.getCreatedAt() != null) {
            response.setCreatedAt(java.time.LocalDateTime.ofInstant(song.getCreatedAt(), java.time.ZoneId.systemDefault()));
        }
        return response;
    }
}

