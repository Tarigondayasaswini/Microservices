package com.revplay.catalogservice.repository;

import com.revplay.catalogservice.entity.Podcast;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface PodcastRepository extends JpaRepository<Podcast, Long> {
    List<Podcast> findByArtistId(@Param("artistId") Long artistId);

    List<Podcast> findByCategoryId(Long categoryId);

    Page<Podcast> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title, Pageable pageable);
    
    Page<Podcast> findByArtistIdAndIsActiveTrue(@Param("artistId") Long artistId, Pageable pageable);

    long countByArtistIdAndIsActiveTrue(@Param("artistId") Long artistId);
    
    @Query("SELECT p FROM Podcast p WHERE p.isActive = true ORDER BY p.createdAt DESC")
    Page<Podcast> findRecommended(Pageable pageable);
}
