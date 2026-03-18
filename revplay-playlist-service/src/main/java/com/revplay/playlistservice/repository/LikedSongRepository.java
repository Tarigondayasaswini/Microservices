package com.revplay.playlistservice.repository;

import com.revplay.playlistservice.entity.LikedSong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikedSongRepository extends JpaRepository<LikedSong, Long> {
    Page<LikedSong> findByUserIdOrderByLikedAtDesc(Long userId, Pageable pageable);

    Optional<LikedSong> findByUserIdAndSongId(Long userId, Long songId);

    boolean existsByUserIdAndSongId(Long userId, Long songId);
}
