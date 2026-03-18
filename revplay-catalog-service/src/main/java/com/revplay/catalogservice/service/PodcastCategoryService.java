package com.revplay.catalogservice.service;


import java.util.List;

import com.revplay.catalogservice.dto.request.PodcastCategoryCreateRequest;
import com.revplay.catalogservice.dto.response.PodcastCategoryResponse;

public interface PodcastCategoryService {

    PodcastCategoryResponse create(PodcastCategoryCreateRequest request);

    List<PodcastCategoryResponse> list();
}

