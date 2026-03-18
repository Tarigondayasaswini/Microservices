package com.revplay.playbackservice.service;

import com.revplay.playbackservice.dto.request.QueueAddRequest;
import com.revplay.playbackservice.dto.request.QueueReorderRequest;
import com.revplay.playbackservice.dto.response.QueueItemResponse;
import java.util.List;

public interface QueueService {

    QueueItemResponse addToQueue(QueueAddRequest request);

    List<QueueItemResponse> getQueue(Long userId);

    void removeFromQueue(Long queueId);

    List<QueueItemResponse> reorder(QueueReorderRequest request);

    QueueItemResponse next(Long userId, Long currentQueueId);

    QueueItemResponse previous(Long userId, Long currentQueueId);
}


