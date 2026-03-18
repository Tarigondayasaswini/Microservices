package com.revplay.catalogservice.repository;



import java.util.List;

import com.revplay.catalogservice.entity.ArtistSocialLink;
import com.revplay.catalogservice.enums.SocialPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistSocialLinkRepository extends JpaRepository<ArtistSocialLink, Long> {
    List<ArtistSocialLink> findByArtistId(Long artistId);
    boolean existsByArtistIdAndPlatform(Long artistId, SocialPlatform platform);
    boolean existsByArtistIdAndPlatformAndLinkIdNot(Long artistId, SocialPlatform platform, Long linkId);
}

