package com.revplay.catalogservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PlaybackValidationException extends RuntimeException {
    public PlaybackValidationException(String message) { super(message); }
}
