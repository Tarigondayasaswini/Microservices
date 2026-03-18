package com.revplay.playbackservice.dto.request;

import com.revplay.playbackservice.validation.ValidQueueContentSelection;
import jakarta.validation.constraints.NotNull;

@ValidQueueContentSelection
public record QueueAddRequest(
        @NotNull Long userId,
        Long songId,
        Long episodeId
) {
}










