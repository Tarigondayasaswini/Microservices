package com.revplay.playlistservice.repository;

import com.revplay.playlistservice.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserIdAndIsActiveTrue(Long userId);

    Page<Playlist> findByIsPublicTrueAndIsActiveTrue(Pageable pageable);

    Page<Playlist> findByNameContainingIgnoreCaseAndIsPublicTrueAndIsActiveTrue(String name, Pageable pageable);

    Page<Playlist> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);

    @Query("SELECT p FROM Playlist p WHERE (p.isPublic = true OR p.userId = :userId) AND p.isActive = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Playlist> searchPlaylistsForUser(@Param("keyword") String keyword, @Param("userId") Long userId, Pageable pageable);

    default Page<Playlist> searchPublicPlaylistsByKeyword(String keyword, Pageable pageable) {
        return findByNameContainingIgnoreCaseAndIsPublicTrueAndIsActiveTrue(keyword, pageable);
    }
}
