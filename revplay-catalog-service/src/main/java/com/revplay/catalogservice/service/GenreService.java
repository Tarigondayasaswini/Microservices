package com.revplay.catalogservice.service;

import com.revplay.catalogservice.dto.request.GenreUpsertRequest;
import com.revplay.catalogservice.dto.response.GenreResponse;
import java.util.List;

public interface GenreService {

    List<GenreResponse> getAll();

    GenreResponse getById(Long genreId);

    GenreResponse create(GenreUpsertRequest request);

    GenreResponse update(Long genreId, GenreUpsertRequest request);

    void delete(Long genreId);
}



