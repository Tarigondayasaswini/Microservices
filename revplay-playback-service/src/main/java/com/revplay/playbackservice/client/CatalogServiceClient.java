package com.revplay.playbackservice.client;

import com.revplay.playbackservice.dto.response.ApiResponse;
import com.revplay.playbackservice.dto.response.SongResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", path = "/api/v1")
public interface CatalogServiceClient {

    @GetMapping("/songs/{id}")
    ResponseEntity<ApiResponse<SongResponse>> getSongById(@PathVariable("id") java.lang.Long id);

    @GetMapping("/songs/{id}/exists")
    ResponseEntity<ApiResponse<java.lang.Boolean>> isSongActive(@PathVariable("id") java.lang.Long id);
}
