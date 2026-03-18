package com.revplay.playbackservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "playback_history", indexes = {
        @Index(name = "idx_playback_user", columnList = "user_id"),
        @Index(name = "idx_playback_song", columnList = "song_id")
})
@Getter
@Setter
public class PlaybackHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "song_id", nullable = false)
    private Long songId;

    @Column(name = "played_at", nullable = false)
    private Instant playedAt;

    @Column(name = "duration_listened_seconds", nullable = false)
    private Integer durationListenedSeconds;

    @PrePersist
    protected void onCreate() {
        if (playedAt == null) {
            playedAt = Instant.now();
        }
    }
}
