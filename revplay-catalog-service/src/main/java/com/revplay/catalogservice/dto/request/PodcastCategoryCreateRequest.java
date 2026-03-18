package com.revplay.catalogservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PodcastCategoryCreateRequest {
    @NotBlank
    private String name;

    private String description;
}

