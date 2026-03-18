package com.revplay.catalogservice.dto.response;

public record GenreResponse(
        Long genreId,
        String name,
        String description,
        Boolean isActive
) {
}

