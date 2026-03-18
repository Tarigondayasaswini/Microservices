package com.revplay.catalogservice.dto.response;





import com.revplay.catalogservice.enums.SocialPlatform;
import lombok.Data;

@Data
public class ArtistSocialLinkResponse {
    private Long linkId;
    private Long artistId;
    private SocialPlatform platform;
    private String url;
}

