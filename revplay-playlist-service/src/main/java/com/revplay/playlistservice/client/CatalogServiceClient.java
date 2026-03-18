package com.revplay.playlistservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Note: Requires the shared ApiResponse to be mapped properly,
// or we can just expect raw Object/Map representation if we don't recreate the entire DTO.
// For simplicity we use a plain Map or a minimal SongDto representation from the catalog service.

@FeignClient(name = "catalog-service", path = "/api/v1")
public interface CatalogServiceClient {

    @GetMapping("/songs/{id}")
    ResponseEntity<Object> getSongById(@PathVariable("id") Long id);

    @GetMapping("/albums/{id}")
    ResponseEntity<Object> getAlbumById(@PathVariable("id") Long id);

    @GetMapping("/podcasts/{id}")
    ResponseEntity<Object> getPodcastById(@PathVariable("id") Long id);
}
