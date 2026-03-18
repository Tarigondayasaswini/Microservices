package com.revplay.playlistservice.exception;

public class AuthForbiddenException extends RuntimeException {
    public AuthForbiddenException(String message) {
        super(message);
    }
}
