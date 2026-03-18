package com.revplay.analyticsservice.service;

import com.revplay.analyticsservice.dto.response.ArtistDashboardResponse;
import com.revplay.analyticsservice.dto.response.ListeningTrendPointResponse;
import com.revplay.analyticsservice.dto.response.SongPopularityResponse;
import com.revplay.analyticsservice.dto.response.TopListenerResponse;
import com.revplay.analyticsservice.enums.TrendRange;
import com.revplay.analyticsservice.dto.response.FavoritedUserResponse;
import com.revplay.analyticsservice.dto.response.SongPlayCountResponse;

import java.time.LocalDate;
import java.util.List;

public interface ArtistAnalyticsService {

    ArtistDashboardResponse dashboard(Long artistId);

    SongPlayCountResponse songPlayCount(Long artistId, Long songId);

    List<SongPopularityResponse> songPopularity(Long artistId);

    List<FavoritedUserResponse> usersWhoFavoritedSongs(Long artistId);

    List<ListeningTrendPointResponse> listeningTrends(Long artistId, TrendRange range, LocalDate from, LocalDate to);

    List<TopListenerResponse> topListeners(Long artistId, int limit);
}



