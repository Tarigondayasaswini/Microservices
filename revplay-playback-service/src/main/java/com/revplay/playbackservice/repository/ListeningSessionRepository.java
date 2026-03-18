package com.revplay.playbackservice.repository;

import com.revplay.playbackservice.entity.ListeningSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListeningSessionRepository extends JpaRepository<ListeningSession, Long> {
    Page<ListeningSession> findByUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);

    Optional<ListeningSession> findTopByUserIdOrderByStartedAtDesc(Long userId);
}
