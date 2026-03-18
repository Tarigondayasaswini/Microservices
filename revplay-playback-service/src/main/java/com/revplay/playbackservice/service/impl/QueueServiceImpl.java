package com.revplay.playbackservice.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.revplay.playbackservice.dto.request.QueueAddRequest;
import com.revplay.playbackservice.dto.request.QueueReorderRequest;
import com.revplay.playbackservice.dto.response.QueueItemResponse;
import com.revplay.playbackservice.entity.QueueItem;
import com.revplay.playbackservice.exception.PlaybackNotFoundException;
import com.revplay.playbackservice.exception.PlaybackValidationException;
import com.revplay.playbackservice.mapper.QueueItemMapper;
import com.revplay.playbackservice.repository.QueueItemRepository;
import com.revplay.playbackservice.service.QueueService;
import com.revplay.playbackservice.util.PlaybackValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QueueServiceImpl implements QueueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueServiceImpl.class);

    private final QueueItemRepository queueItemRepository;
    private final QueueItemMapper queueItemMapper;

    public QueueServiceImpl(
            QueueItemRepository queueItemRepository,
            QueueItemMapper queueItemMapper
    ) {
        this.queueItemRepository = queueItemRepository;
        this.queueItemMapper = queueItemMapper;
    }

    @Transactional
    public QueueItemResponse addToQueue(QueueAddRequest request) {
        LOGGER.info("Adding content to queue for userId={}", request == null ? null : request.userId());
        validateQueueRequest(request);
        int nextPosition = queueItemRepository.findTopByUserIdOrderByPositionDesc(request.userId())
                .map(item -> item.getPosition() + 1)
                .orElse(1);

        QueueItem entity = new QueueItem();
        entity.setUserId(request.userId());
        entity.setSongId(request.songId());
        entity.setEpisodeId(request.episodeId());
        entity.setPosition(nextPosition);
        entity.setCreatedAt(Instant.now());

        QueueItem saved = queueItemRepository.save(entity);
        return queueItemMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<QueueItemResponse> getQueue(Long userId) {
        LOGGER.info("Fetching queue for userId={}", userId);
        return queueItemRepository.findByUserIdOrderByPositionAscQueueIdAsc(userId)
                .stream()
                .map(queueItemMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeFromQueue(Long queueId) {
        LOGGER.info("Removing queue item queueId={}", queueId);
        QueueItem entity = queueItemRepository.findById(queueId)
                .orElseThrow(() -> new PlaybackNotFoundException("Queue item " + queueId + " not found"));
        queueItemRepository.delete(entity);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<QueueItemResponse> reorder(QueueReorderRequest request) {
        LOGGER.info("Reordering queue for userId={}", request == null ? null : request.userId());
        if (request.userId() == null || request.queueIdsInOrder() == null || request.queueIdsInOrder().isEmpty()) {
            throw new PlaybackValidationException("userId and queueIdsInOrder are required");
        }

        List<QueueItem> userQueue = queueItemRepository.findByUserIdForUpdate(request.userId());
        Map<Long, QueueItem> byId = userQueue.stream()
                .collect(Collectors.toMap(QueueItem::getQueueId, Function.identity()));

        Set<Long> inputIds = new HashSet<>(request.queueIdsInOrder());
        if (inputIds.size() != request.queueIdsInOrder().size()) {
            throw new PlaybackValidationException("queueIdsInOrder contains duplicate queue IDs");
        }

        List<QueueItem> ordered = new ArrayList<>();
        for (Long queueId : request.queueIdsInOrder()) {
            QueueItem item = byId.get(queueId);
            if (item == null) {
                throw new PlaybackValidationException("Queue item " + queueId + " does not belong to user " + request.userId());
            }
            ordered.add(item);
        }

        int position = 1;
        for (QueueItem item : ordered) {
            item.setPosition(position++);
        }
        queueItemRepository.saveAll(ordered);
        return getQueue(request.userId());
    }

    @Transactional(readOnly = true)
    public QueueItemResponse next(Long userId, Long currentQueueId) {
        LOGGER.info("Navigating to next queue item for userId={}, currentQueueId={}", userId, currentQueueId);
        return navigate(userId, currentQueueId, true);
    }

    @Transactional(readOnly = true)
    public QueueItemResponse previous(Long userId, Long currentQueueId) {
        LOGGER.info("Navigating to previous queue item for userId={}, currentQueueId={}", userId, currentQueueId);
        return navigate(userId, currentQueueId, false);
    }

    private void validateQueueRequest(QueueAddRequest request) {
        if (request == null || request.userId() == null) {
            throw new PlaybackValidationException("userId is required");
        }
        PlaybackValidationUtil.requireExactlyOneContentId(request.songId(), request.episodeId());
    }

    private QueueItemResponse navigate(Long userId, Long currentQueueId, boolean forward) {
        if (currentQueueId == null) {
            throw new PlaybackValidationException("currentQueueId is required");
        }
        List<QueueItemResponse> queue = getQueue(userId);
        if (queue.isEmpty()) {
            throw new PlaybackNotFoundException("Queue is empty for user " + userId);
        }
        int currentIndex = -1;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).queueId().equals(currentQueueId)) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex < 0) {
            throw new PlaybackNotFoundException("Queue item " + currentQueueId + " not found for user " + userId);
        }
        int nextIndex = forward
                ? (currentIndex + 1) % queue.size()
                : (currentIndex - 1 + queue.size()) % queue.size();
        return queue.get(nextIndex);
    }


}





