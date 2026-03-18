package com.revplay.catalogservice.repository;

import com.revplay.catalogservice.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByArtistId(@Param("artistId") Long artistId);

    Page<Song> findByArtistIdAndIsActiveTrue(@Param("artistId") Long artistId, Pageable pageable);

    long countByArtistIdAndIsActiveTrue(@Param("artistId") Long artistId);


    List<Song> findByAlbumId(Long albumId);

    Page<Song> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    List<Song> findByVisibilityAndIsActiveTrue(String visibility);

    Page<Song> findByTitleContainingIgnoreCaseAndVisibilityAndIsActiveTrue(String title, String visibility,
            Pageable pageable);
    long countByAlbumId(Long albumId);
    boolean existsByAlbumIdAndTitleIgnoreCaseAndIsActiveTrue(Long albumId, String title);
    boolean existsByAlbumIdAndTitleIgnoreCaseAndIsActiveTrueAndSongIdNot(Long albumId, String title, Long songId);
    boolean existsBySongIdAndIsActiveTrue(Long songId);
}
