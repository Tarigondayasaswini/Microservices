package com.revplay.catalogservice.service.impl;

import com.revplay.catalogservice.dto.request.PodcastCategoryCreateRequest;
import com.revplay.catalogservice.dto.response.PodcastCategoryResponse;
import com.revplay.catalogservice.entity.PodcastCategory;
import com.revplay.catalogservice.exception.ConflictException;
import com.revplay.catalogservice.mapper.PodcastCategoryMapper;
import com.revplay.catalogservice.repository.PodcastCategoryRepository;
import com.revplay.catalogservice.service.PodcastCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PodcastCategoryServiceImpl implements PodcastCategoryService {
    private final PodcastCategoryRepository repository;
    private final PodcastCategoryMapper mapper;

    @Override
    @Transactional
    public PodcastCategoryResponse create(PodcastCategoryCreateRequest request) {
        log.info("Creating podcast category name={}", request.getName());
        if (repository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Category", "name", request.getName());
        }
        PodcastCategory category = mapper.toEntity(request);
        return mapper.toResponse(repository.save(category));
    }

    @Override
    public List<PodcastCategoryResponse> list() {
        log.info("Listing podcast categories");
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
