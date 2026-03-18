package com.revplay.playlistservice.service;

import com.revplay.playlistservice.dto.request.CreatePlaylistRequest;
import com.revplay.playlistservice.dto.request.UpdatePlaylistRequest;
import com.revplay.playlistservice.dto.response.PlaylistDetailResponse;
import com.revplay.playlistservice.dto.response.PlaylistResponse;
import com.revplay.playlistservice.dto.response.PlaylistSongResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PlaylistService {
    PlaylistResponse createPlaylist(Long userId, CreatePlaylistRequest request);

    PlaylistResponse updatePlaylist(Long userId, Long playlistId, UpdatePlaylistRequest request);

    void deletePlaylist(Long userId, Long playlistId);

    PlaylistDetailResponse getPlaylistById(Long userId, Long playlistId);

    List<PlaylistResponse> getUserPlaylists(Long userId);

    PlaylistSongResponse addSongToPlaylist(Long userId, Long playlistId, Long songId);

    void removeSongFromPlaylist(Long userId, Long playlistId, Long songId);

    Page<PlaylistResponse> getPublicPlaylists(Pageable pageable);

    Page<PlaylistResponse> searchPlaylists(String keyword, Pageable pageable);
}
