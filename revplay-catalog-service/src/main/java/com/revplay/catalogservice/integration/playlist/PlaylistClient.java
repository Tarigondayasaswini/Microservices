package com.revplay.catalogservice.integration.playlist;

import com.revplay.catalogservice.common.response.ApiResponse;
import com.revplay.catalogservice.common.dto.PagedResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "playlist-service")
public interface PlaylistClient {

    @GetMapping("/api/v1/playlists/search")
    ApiResponse<PagedResponseDto<PlaylistResponse>> searchPlaylists(
            @RequestParam("keyword") String keyword,
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}
