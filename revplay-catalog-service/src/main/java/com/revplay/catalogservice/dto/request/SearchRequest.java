package com.revplay.catalogservice.dto.request;

import com.revplay.catalogservice.enums.SearchContentType;
import java.time.LocalDate;

public record SearchRequest(
        String query,
        SearchContentType type,
        Long genreId,
        LocalDate releaseDateFrom,
        LocalDate releaseDateTo,
        String artistType,
        int page,
        int size,
        String sortBy,
        String sortDir
) {
}


