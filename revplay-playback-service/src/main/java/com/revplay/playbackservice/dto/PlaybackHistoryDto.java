package com.revplay.playbackservice.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaybackHistoryDto {
    private Long id;
    private Long userId;
    private Long songId;
    private Instant playedAt;
    private Integer durationListenedSeconds;
}
