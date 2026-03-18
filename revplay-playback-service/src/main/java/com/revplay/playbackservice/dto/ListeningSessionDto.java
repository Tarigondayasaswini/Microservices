package com.revplay.playbackservice.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListeningSessionDto {
    private Long id;
    private Long userId;
    private Instant startedAt;
    private Instant endedAt;
    private String deviceInfo;
}
