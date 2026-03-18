package com.revplay.playbackservice.mapper;

import com.revplay.playbackservice.dto.response.PlayHistoryResponse;
import com.revplay.playbackservice.entity.PlayHistory;
import org.springframework.stereotype.Component;

@Component
public class PlayHistoryMapper {

    public PlayHistoryResponse toDto(PlayHistory entity) {
        return new PlayHistoryResponse(
                entity.getPlayId(),
                entity.getUserId(),
                entity.getSongId(),
                entity.getEpisodeId(),
                entity.getPlayedAt(),
                entity.getCompleted(),
                entity.getPlayDurationSeconds()
        );
    }
}


