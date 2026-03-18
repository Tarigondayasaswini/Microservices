package com.revplay.catalogservice.dto.response;

public record TopArtistItemResponse(
        Long artistId,
        String displayName,
        String artistType,
        Long playCount
) {
}


