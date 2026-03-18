package com.revplay.playlistservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikedSongResponse {
    private Long id;
    private Long userId;
    private Long songId;
    private LocalDateTime likedAt;
}
