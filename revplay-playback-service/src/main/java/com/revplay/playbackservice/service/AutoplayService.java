package com.revplay.playbackservice.service;

import com.revplay.playbackservice.dto.response.SongResponse;

public interface AutoplayService {

    SongResponse getNextSong(Long userId, Long currentSongId);
}
