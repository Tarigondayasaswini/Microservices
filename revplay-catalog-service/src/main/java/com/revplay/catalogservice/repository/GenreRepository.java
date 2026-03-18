package com.revplay.catalogservice.repository;

import com.revplay.catalogservice.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByNameIgnoreCase(String name);
    long countByGenreIdIn(java.util.List<Long> genreIds);
    java.util.List<Genre> findByIsActiveTrueOrderByNameAscGenreIdAsc();
    java.util.Optional<Genre> findByGenreIdAndIsActiveTrue(Long id);
    boolean existsByNameIgnoreCaseAndIsActiveTrue(String name);
    java.util.Optional<Genre> findByNameIgnoreCaseAndIsActiveFalse(String name);
    boolean existsByNameIgnoreCaseAndIsActiveTrueAndGenreIdNot(String name, Long id);
}
