package com.revplay.catalogservice.service;

import com.revplay.catalogservice.dto.request.SearchRequest;
import com.revplay.catalogservice.dto.response.SearchResultItemResponse;
import com.revplay.catalogservice.common.dto.PagedResponseDto;

public interface SearchService {

    PagedResponseDto<SearchResultItemResponse> search(SearchRequest request);
}



