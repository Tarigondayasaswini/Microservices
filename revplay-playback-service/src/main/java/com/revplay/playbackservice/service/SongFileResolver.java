package com.revplay.playbackservice.service;

import org.springframework.core.io.Resource;

public interface SongFileResolver {

    Resource loadSongResource(String fileUrl);
}

