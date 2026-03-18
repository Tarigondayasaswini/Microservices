package com.revplay.catalogservice.dto.request;


import com.revplay.catalogservice.enums.ContentVisibility;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SongVisibilityRequest {
    private Boolean isActive;

    @NotNull
    private ContentVisibility visibility;
}

