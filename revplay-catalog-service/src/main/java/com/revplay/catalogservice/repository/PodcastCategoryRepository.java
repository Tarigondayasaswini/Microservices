package com.revplay.catalogservice.repository;

import com.revplay.catalogservice.entity.PodcastCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PodcastCategoryRepository extends JpaRepository<PodcastCategory, Long> {
    Optional<PodcastCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
