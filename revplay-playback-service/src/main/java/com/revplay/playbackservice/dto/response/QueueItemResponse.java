package com.revplay.playbackservice.dto.response;

import java.time.Instant;

public record QueueItemResponse(
        Long queueId,
        Long userId,
        Long songId,
        Long episodeId,
        Integer position,
        Instant createdAt
) {
}





