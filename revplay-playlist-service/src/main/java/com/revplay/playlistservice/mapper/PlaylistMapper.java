package com.revplay.playlistservice.mapper;

import com.revplay.playlistservice.dto.request.CreatePlaylistRequest;
import com.revplay.playlistservice.dto.request.UpdatePlaylistRequest;
import com.revplay.playlistservice.dto.response.PlaylistDetailResponse;
import com.revplay.playlistservice.dto.response.PlaylistResponse;
import com.revplay.playlistservice.dto.response.PlaylistSongResponse;
import com.revplay.playlistservice.entity.Playlist;
import com.revplay.playlistservice.entity.PlaylistSong;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class PlaylistMapper {


    public Playlist toEntity(CreatePlaylistRequest request, Long userId) {
        return Playlist.builder()
                .userId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();
    }

    public void updateEntity(Playlist playlist, UpdatePlaylistRequest request) {
        if (request.getName() != null) {
            playlist.setName(request.getName());
        }
        if (request.getDescription() != null) {
            playlist.setDescription(request.getDescription());
        }
        if (request.getIsPublic() != null) {
            playlist.setIsPublic(request.getIsPublic());
        }
    }


    public PlaylistResponse toResponse(Playlist playlist, long songCount, long followerCount) {
        return PlaylistResponse.builder()
                .id(playlist.getId())
                .userId(playlist.getUserId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .isPublic(playlist.getIsPublic())
                .songCount(songCount)
                .followerCount(followerCount)
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .build();
    }


    public PlaylistDetailResponse toDetailResponse(Playlist playlist, List<PlaylistSong> songs, long songCount, long followerCount) {
        return PlaylistDetailResponse.builder()
                .id(playlist.getId())
                .userId(playlist.getUserId())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .isPublic(playlist.getIsPublic())
                .songCount(songCount)
                .followerCount(followerCount)
                .createdAt(playlist.getCreatedAt())
                .updatedAt(playlist.getUpdatedAt())
                .songs(songs.stream().map(this::toSongResponse).collect(Collectors.toList()))
                .build();
    }

    public PlaylistSongResponse toSongResponse(PlaylistSong ps) {
        return PlaylistSongResponse.builder()
                .id(ps.getId())
                .playlistId(ps.getPlaylist().getId())
                .songId(ps.getSongId())
                .position(ps.getPosition())
                .addedAt(ps.getAddedAt())
                .build();
    }
}
