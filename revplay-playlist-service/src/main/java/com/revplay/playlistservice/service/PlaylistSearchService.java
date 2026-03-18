package com.revplay.playlistservice.service;

import com.revplay.playlistservice.common.response.PagedResponseDto;
import com.revplay.playlistservice.dto.response.PlaylistResponse;

public interface PlaylistSearchService {

    PagedResponseDto<PlaylistResponse> searchPublicPlaylists(String keyword, int page, int size);
}

