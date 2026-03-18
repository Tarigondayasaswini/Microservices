package com.revplay.catalogservice.dto.request;



import com.revplay.catalogservice.enums.ArtistType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArtistCreateRequest {
    @NotBlank
    @Size(max = 120)
    private String displayName;

    @Size(max = 1000)
    private String bio;

    @Size(max = 2048)
    private String bannerImageUrl;

    @NotNull
    private ArtistType artistType;
}

