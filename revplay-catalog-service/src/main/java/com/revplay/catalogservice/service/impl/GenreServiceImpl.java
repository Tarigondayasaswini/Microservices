package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.service.GenreService;
import com.revplay.catalogservice.dto.request.GenreUpsertRequest;
import com.revplay.catalogservice.dto.response.GenreResponse;
import com.revplay.catalogservice.entity.Genre;
import com.revplay.catalogservice.exception.BadRequestException;
import com.revplay.catalogservice.exception.ConflictException;
import com.revplay.catalogservice.exception.ResourceNotFoundException;
import com.revplay.catalogservice.repository.GenreRepository;
import com.revplay.catalogservice.util.DiscoveryValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "genres", key = "'all'")
    public List<GenreResponse> getAll() {
        log.debug("Fetching all active genres");
        return genreRepository.findByIsActiveTrueOrderByNameAscGenreIdAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "genres", key = "'id:' + #genreId")
    public GenreResponse getById(Long genreId) {
        log.debug("Fetching genre by id={}", genreId);
        DiscoveryValidationUtil.requirePositiveId(genreId, "genreId");
        Genre genre = genreRepository.findByGenreIdAndIsActiveTrue(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", genreId));
        return toResponse(genre);
    }

    @Transactional
    public GenreResponse create(GenreUpsertRequest request) {
        log.info("Creating genre: {}", request.name());
        String normalizedName = normalizeName(request.name());
        String normalizedDescription = normalizeDescription(request.description());

        if (genreRepository.existsByNameIgnoreCaseAndIsActiveTrue(normalizedName)) {
            log.warn("Genre name already exists: {}", normalizedName);
            throw new ConflictException("Genre with name '" + normalizedName + "' already exists");
        }

        Genre reactivatable = genreRepository.findByNameIgnoreCaseAndIsActiveFalse(normalizedName).orElse(null);
        if (reactivatable != null) {
            reactivatable.setIsActive(Boolean.TRUE);
            reactivatable.setDescription(normalizedDescription);
            Genre saved = genreRepository.save(reactivatable);
            log.info("Reactivated existing genre id={}", saved.getGenreId());
            return toResponse(saved);
        }

        Genre entity = new Genre();
        entity.setName(normalizedName);
        entity.setDescription(normalizedDescription);
        entity.setIsActive(Boolean.TRUE);
        Genre saved = genreRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(cacheNames = "genres", allEntries = true)
    public GenreResponse update(Long genreId, GenreUpsertRequest request) {
        log.info("Updating genre id={}", genreId);
        DiscoveryValidationUtil.requirePositiveId(genreId, "genreId");
        String normalizedName = normalizeName(request.name());
        String normalizedDescription = normalizeDescription(request.description());

        Genre genre = genreRepository.findByGenreIdAndIsActiveTrue(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", genreId));

        if (genreRepository.existsByNameIgnoreCaseAndIsActiveTrueAndGenreIdNot(normalizedName, genreId)) {
            log.warn("Genre name already exists for update, genreId={}", genreId);
            throw new ConflictException("Genre name already exists");
        }

        genre.setName(normalizedName);
        genre.setDescription(normalizedDescription);
        Genre saved = genreRepository.save(genre);
        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(cacheNames = "genres", allEntries = true)
    public void delete(Long genreId) {
        log.info("Soft deleting genre id={}", genreId);
        DiscoveryValidationUtil.requirePositiveId(genreId, "genreId");

        Genre genre = genreRepository.findByGenreIdAndIsActiveTrue(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", genreId));

        genre.setIsActive(Boolean.FALSE);
        genreRepository.save(genre);
    }

    private String normalizeName(String name) {
        DiscoveryValidationUtil.requireNotBlank(name, "name");
        String normalized = name.trim();
        if (normalized.length() > 100) {
            throw new BadRequestException("name must be at most 100 characters");
        }
        return normalized;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String normalized = description.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.length() > 1000) {
            throw new BadRequestException("description must be at most 1000 characters");
        }
        return normalized;
    }

    private GenreResponse toResponse(Genre entity) {
        return new GenreResponse(
                entity.getGenreId(),
                entity.getName(),
                entity.getDescription(),
                entity.getIsActive()
        );
    }
}
