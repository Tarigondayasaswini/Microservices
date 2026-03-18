package com.revplay.analyticsservice.repository;

import com.revplay.analyticsservice.entity.UserAdPlaybackState;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAdPlaybackStateRepository extends JpaRepository<UserAdPlaybackState, Long> {

    Optional<UserAdPlaybackState> findByUserId(Long userId);
}

