package com.revplay.catalogservice.integration.playlist;

import com.revplay.catalogservice.common.dto.PagedResponseDto;
import com.revplay.catalogservice.common.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistSearchService {

    private final PlaylistClient playlistClient;

    public PlaylistSearchService(PlaylistClient playlistClient) {
        this.playlistClient = playlistClient;
    }

    public Page<PlaylistResponse> searchPlaylists(String query, Pageable pageable) {
        try {
            ApiResponse<PagedResponseDto<PlaylistResponse>> apiResponse = 
                playlistClient.searchPlaylists(query, pageable.getPageNumber(), pageable.getPageSize());
            if (apiResponse != null && apiResponse.getData() != null) {
                PagedResponseDto<PlaylistResponse> data = apiResponse.getData();
                return new PageImpl<>(data.getContent(), pageable, data.getTotalElements());
            }
        } catch (Exception e) {
            // Log error if needed, but return empty page to avoid breaking overall search
        }
        return new PageImpl<>(List.of(), pageable, 0);
    }

    public PagedResponseDto<PlaylistResponse> searchPublicPlaylists(String keyword, int page, int size) {
        try {
            ApiResponse<PagedResponseDto<PlaylistResponse>> apiResponse = playlistClient.searchPlaylists(keyword, page, size);
            if (apiResponse != null && apiResponse.getData() != null) {
                return apiResponse.getData();
            }
            return PagedResponseDto.empty(page, size, "name", "ASC");
        } catch (Exception e) {
            return PagedResponseDto.empty(page, size, "name", "ASC");
        }
    }
}
