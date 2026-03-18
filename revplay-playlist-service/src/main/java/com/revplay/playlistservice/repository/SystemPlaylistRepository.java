package com.revplay.playlistservice.repository;

import com.revplay.playlistservice.entity.SystemPlaylist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemPlaylistRepository extends JpaRepository<SystemPlaylist, Long> {

    List<SystemPlaylist> findByIsActiveTrueAndDeletedAtIsNull();

    Optional<SystemPlaylist> findBySlugAndDeletedAtIsNull(String slug);
}

