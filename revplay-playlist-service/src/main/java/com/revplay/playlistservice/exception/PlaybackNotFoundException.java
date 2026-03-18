package com.revplay.playlistservice.exception;

public class PlaybackNotFoundException extends RuntimeException {
    public PlaybackNotFoundException(String message) {
        super(message);
    }
}
