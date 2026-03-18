package com.revplay.playbackservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordPlaybackRequest {
    @NotNull(message = "Song ID is required")
    private Long songId;

    @NotNull(message = "Duration listened is required")
    private Integer durationListenedSeconds;
}
