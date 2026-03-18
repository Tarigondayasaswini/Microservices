package com.revplay.catalogservice.repository;



import java.util.List;

import com.revplay.catalogservice.entity.SongGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongGenreRepository extends JpaRepository<SongGenre, Long> {
    List<SongGenre> findBySongId(Long songId);
    boolean existsBySongIdAndGenreId(Long songId, Long genreId);
    void deleteBySongId(Long songId);
}
