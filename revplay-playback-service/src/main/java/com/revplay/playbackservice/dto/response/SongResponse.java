package com.revplay.playbackservice.dto.response;

public record SongResponse(
    Long id,
    String title,
    String artist,
    String album,
    String genre,
    String duration,
    String fileUrl,
    String coverArtPath
) {
    public String getTitle() {
        return title;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
