package com.example.exception;

public class CustomJwtException extends RuntimeException {

    public CustomJwtException(String message) {
        super(message);
    }
}
