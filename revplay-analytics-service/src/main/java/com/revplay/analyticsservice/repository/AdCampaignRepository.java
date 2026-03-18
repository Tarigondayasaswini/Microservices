package com.revplay.analyticsservice.repository;

import com.revplay.analyticsservice.entity.AdCampaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdCampaignRepository extends JpaRepository<AdCampaign, Long> {
    Page<AdCampaign> findByIsActiveTrue(Pageable pageable);

    // Fetch a random active ad
    @Query(nativeQuery = true, value = "SELECT * FROM ad_campaigns WHERE is_active = true ORDER BY RAND() LIMIT 1")
    Optional<AdCampaign> findRandomActiveAd();
}
