package com.revplay.playbackservice.repository;

import com.revplay.playbackservice.entity.SongDownload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongDownloadRepository extends JpaRepository<SongDownload, Long> {

    boolean existsByUserIdAndSongId(Long userId, Long songId);
}

