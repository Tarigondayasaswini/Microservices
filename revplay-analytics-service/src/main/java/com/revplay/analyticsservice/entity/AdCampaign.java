package com.revplay.analyticsservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "ad_campaigns", indexes = {
        @Index(name = "idx_ad_campaigns_active", columnList = "is_active")
})
@Getter
@Setter
public class AdCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "sponsor_name", nullable = false, length = 255)
    private String sponsorName;

    @Column(name = "audio_url", nullable = false, length = 1000)
    private String audioUrl;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "target_url", length = 1000)
    private String targetUrl;

    @Column(name = "impressions_count", nullable = false)
    private Long impressionsCount = 0L;

    @Column(name = "clicks_count", nullable = false)
    private Long clicksCount = 0L;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = Instant.now();
        if (isActive == null)
            isActive = true;
        if (impressionsCount == null)
            impressionsCount = 0L;
        if (clicksCount == null)
            clicksCount = 0L;
    }
}
