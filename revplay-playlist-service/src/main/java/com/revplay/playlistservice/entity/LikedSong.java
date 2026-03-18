package com.revplay.playlistservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "liked_songs", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "song_id" })
}, indexes = {
        @Index(name = "idx_liked_songs_user", columnList = "user_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // user-service reference

    @Column(name = "song_id", nullable = false)
    private Long songId; // catalog-service reference

    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt;

    @PrePersist
    protected void onCreate() {
        likedAt = LocalDateTime.now();
    }
}
