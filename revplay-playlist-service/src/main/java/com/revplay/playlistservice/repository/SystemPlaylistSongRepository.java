package com.revplay.playlistservice.repository;

import com.revplay.playlistservice.entity.SystemPlaylistSong;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemPlaylistSongRepository extends JpaRepository<SystemPlaylistSong, Long> {

    List<SystemPlaylistSong> findBySystemPlaylist_IdAndDeletedAtIsNullOrderByPositionAsc(Long playlistId);

    boolean existsBySystemPlaylist_IdAndSongIdAndDeletedAtIsNull(Long playlistId, Long songId);
}
