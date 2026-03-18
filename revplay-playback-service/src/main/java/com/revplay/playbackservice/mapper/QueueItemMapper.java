package com.revplay.playbackservice.mapper;

import com.revplay.playbackservice.dto.response.QueueItemResponse;
import com.revplay.playbackservice.entity.QueueItem;
import org.springframework.stereotype.Component;

@Component
public class QueueItemMapper {

    public QueueItemResponse toDto(QueueItem entity) {
        return new QueueItemResponse(
                entity.getQueueId(),
                entity.getUserId(),
                entity.getSongId(),
                entity.getEpisodeId(),
                entity.getPosition(),
                entity.getCreatedAt()
        );
    }
}


