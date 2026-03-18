package com.revplay.analyticsservice.dto.response;

public record TopMixResponse(
        String playlistName,
        Long totalPlayCount
) {
}

