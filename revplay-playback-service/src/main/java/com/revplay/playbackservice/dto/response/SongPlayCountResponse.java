package com.revplay.playbackservice.dto.response;

public record SongPlayCountResponse(
        Long songId,
        String title,
        Long playCount
) {
}





