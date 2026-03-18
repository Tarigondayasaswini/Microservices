package com.revplay.catalogservice.dto.response;



import java.time.LocalDateTime;


import com.revplay.catalogservice.enums.ArtistType;
import lombok.Data;

@Data
public class ArtistResponse {
    private Long artistId;
    private Long userId;
    private String displayName;
    private String bio;
    private String bannerImageUrl;
    private ArtistType artistType;
    private Boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

