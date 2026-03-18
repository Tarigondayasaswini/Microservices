package com.revplay.playlistservice.repository;

import com.revplay.playlistservice.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {
    List<PlaylistSong> findByPlaylist_IdOrderByAddedAtDesc(Long playlistId);

    Optional<PlaylistSong> findByPlaylist_IdAndSongId(Long playlistId, Long songId);

    @org.springframework.data.jpa.repository.Query("SELECT MAX(ps.position) FROM PlaylistSong ps WHERE ps.playlist.id = :playlistId")
    Integer findMaxPositionByPlaylistId(Long playlistId);

    long countByPlaylist_Id(Long playlistId);
}
