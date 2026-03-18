package com.revplay.catalogservice.repository;

import com.revplay.catalogservice.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByArtistId(@Param("artistId") Long artistId);

    long countByArtistId(@Param("artistId") Long artistId);

    Page<Album> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title, Pageable pageable);
    
    Page<Album> findByArtistIdAndIsActiveTrue(@Param("artistId") Long artistId, Pageable pageable);
}
