package com.revplay.catalogservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistSocialLinkRequest {
    private Long id;
    
    @NotBlank(message = "Platform is required")
    private String platform;
    
    @NotBlank(message = "URL is required")
    private String url;
}
