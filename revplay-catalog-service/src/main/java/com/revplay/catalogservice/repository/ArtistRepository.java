package com.revplay.catalogservice.repository;

import com.revplay.catalogservice.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findByUserId(Long userId);

    Page<Artist> findByDisplayNameContainingIgnoreCase(String displayName, Pageable pageable);
}
