package com.revplay.playlistservice.service.impl;

import com.revplay.playlistservice.client.CatalogServiceClient;
import com.revplay.playlistservice.exception.InvalidContentReferenceException;
import com.revplay.playlistservice.service.ContentReferenceValidationService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ContentReferenceValidationServiceImpl implements ContentReferenceValidationService {

    private static final Logger log = LoggerFactory.getLogger(ContentReferenceValidationServiceImpl.class);
    private final CatalogServiceClient catalogServiceClient;

    public ContentReferenceValidationServiceImpl(CatalogServiceClient catalogServiceClient) {
        this.catalogServiceClient = catalogServiceClient;
    }

    @Override
    public void validateSongExists(Long songId) {
        try {
            catalogServiceClient.getSongById(songId);
        } catch (FeignException.NotFound e) {
            throw new InvalidContentReferenceException("Song with ID " + songId + " does not exist.");
        } catch (Exception e) {
            log.error("Error validating song {}", songId, e);
            throw new RuntimeException("Validation service unavailable");
        }
    }

    @Override
    public void validateLikeTargetExists(String type, Long targetId) {
        try {
            switch (type.toUpperCase()) {
                case "SONG":
                    catalogServiceClient.getSongById(targetId);
                    break;
                case "ALBUM":
                    catalogServiceClient.getAlbumById(targetId);
                    break;
                case "PODCAST":
                    catalogServiceClient.getPodcastById(targetId);
                    break;
                default:
                    throw new InvalidContentReferenceException("Unsupported likeable type: " + type);
            }
        } catch (FeignException.NotFound e) {
            throw new InvalidContentReferenceException(type + " with ID " + targetId + " does not exist.");
        } catch (Exception e) {
            log.error("Error validating {} with ID {}", type, targetId, e);
            throw new RuntimeException("Validation service unavailable");
        }
    }
}
