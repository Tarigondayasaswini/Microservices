package com.revplay.catalogservice.dto.request;

import java.time.LocalDate;
import com.revplay.catalogservice.enums.ContentVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SongCreateRequest {
    @NotNull(message = "Artist ID is required")
    private Long artistId;

    private Long albumId;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @NotNull(message = "Duration is required")
    @Positive
    private Integer durationSeconds;

    private String fileUrl;

    private ContentVisibility visibility = ContentVisibility.PUBLIC;

    private LocalDate releaseDate;
}
