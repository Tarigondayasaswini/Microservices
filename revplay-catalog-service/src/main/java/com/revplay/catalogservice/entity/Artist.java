package com.revplay.catalogservice.entity;

import com.revplay.catalogservice.enums.ArtistType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "artists", indexes = {
        @Index(name = "idx_artists_user", columnList = "user_id")
})
@Getter
@Setter
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Long artistId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId; // Refers to ID from user-service

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "bio", length = 2000)
    private String bio;

    @Column(name = "banner_image_url", length = 1000)
    private String bannerImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "artist_type", length = 50)
    private ArtistType artistType = ArtistType.MUSIC; // MUSIC, PODCAST, BOTH

    @Column(name = "verified", nullable = false)
    private Boolean verified = Boolean.FALSE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.verified == null)
            this.verified = false;
        if (this.artistType == null)
            this.artistType = ArtistType.MUSIC;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
