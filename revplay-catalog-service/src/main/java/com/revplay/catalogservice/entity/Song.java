package com.revplay.catalogservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "songs", indexes = {
        @Index(name = "idx_songs_artist", columnList = "artist_id"),
        @Index(name = "idx_songs_album", columnList = "album_id"),
        @Index(name = "idx_songs_visibility_active", columnList = "visibility, is_active")
})
@Getter
@Setter
public class Song {
    private static final Logger log = LoggerFactory.getLogger(Song.class);
    static { log.info("ANTIGRAVITY: Song class loaded"); }
    private String antigravityTestField;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @Column(name = "artist_id", nullable = false)
    private Long artistId; // From artist entity in this service

    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "visibility", nullable = false, length = 20)
    private String visibility = "PUBLIC"; // PUBLIC, PRIVATE, UNLISTED

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (visibility == null) {
            visibility = "PUBLIC";
        }
    }
}
