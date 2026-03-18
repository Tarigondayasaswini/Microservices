package com.revplay.playbackservice.dto.response;

import java.time.Instant;

public record PlayHistoryResponse(
        Long playId,
        Long userId,
        Long songId,
        Long episodeId,
        Instant playedAt,
        Boolean completed,
        Integer playDurationSeconds
) {
}





