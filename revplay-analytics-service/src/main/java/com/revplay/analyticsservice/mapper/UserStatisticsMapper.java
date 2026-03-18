package com.revplay.analyticsservice.mapper;

import com.revplay.analyticsservice.dto.response.UserStatisticsResponse;
import com.revplay.analyticsservice.entity.UserStatistics;
import org.springframework.stereotype.Component;

@Component
public class UserStatisticsMapper {

    public UserStatisticsResponse toDto(UserStatistics stats) {
        return new UserStatisticsResponse(
                stats.getUserId(),
                stats.getTotalPlaylists(),
                stats.getTotalFavoriteSongs(),
                stats.getTotalListeningTimeSeconds(),
                stats.getTotalSongsPlayed(),
                stats.getLastUpdated()
        );
    }
}


