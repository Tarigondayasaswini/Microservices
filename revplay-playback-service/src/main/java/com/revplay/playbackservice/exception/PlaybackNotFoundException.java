package com.revplay.playbackservice.exception;

public class PlaybackNotFoundException extends RuntimeException {
    public PlaybackNotFoundException(String message) {
        super(message);
    }
}
