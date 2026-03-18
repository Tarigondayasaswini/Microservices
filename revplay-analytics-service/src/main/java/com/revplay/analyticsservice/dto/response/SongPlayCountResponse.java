package com.revplay.analyticsservice.dto.response;
 
public record SongPlayCountResponse(
        Long songId,
        String title,
        Long playCount
) {
}
