package com.revplay.catalogservice.service;

import com.revplay.catalogservice.dto.response.NewReleaseItemResponse;
import com.revplay.catalogservice.dto.response.PopularPodcastItemResponse;
import com.revplay.catalogservice.dto.response.SearchResultItemResponse;
import com.revplay.catalogservice.dto.response.TopArtistItemResponse;
import com.revplay.catalogservice.common.dto.PagedResponseDto;

public interface BrowseService {

    PagedResponseDto<NewReleaseItemResponse> newReleases(int page, int size, String sortDir);

    PagedResponseDto<TopArtistItemResponse> topArtists(int page, int size);

    PagedResponseDto<PopularPodcastItemResponse> popularPodcasts(int page, int size);

    PagedResponseDto<SearchResultItemResponse> allSongs(int page, int size, String sortBy, String sortDir);

    PagedResponseDto<SearchResultItemResponse> songsByGenre(Long genreId, int page, int size, String sortBy, String sortDir);
}



