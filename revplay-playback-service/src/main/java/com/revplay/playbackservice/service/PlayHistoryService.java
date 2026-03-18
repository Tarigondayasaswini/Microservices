package com.revplay.playbackservice.service;

import com.revplay.playbackservice.dto.request.TrackPlayRequest;
import com.revplay.playbackservice.dto.response.PlayHistoryResponse;

import java.util.List;

public interface PlayHistoryService {

    void trackPlay(TrackPlayRequest request);

    List<PlayHistoryResponse> getHistory(Long userId);

    List<PlayHistoryResponse> recentlyPlayed(Long userId);

    long clearHistory(Long userId);
}



