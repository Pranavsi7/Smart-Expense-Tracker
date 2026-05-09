package com.project.expensetrackerapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centralized exception handler.
 * @ResponseStatus on exception classes is NOT reliable in Spring Boot 3
 * when exceptions propagate through @Transactional proxies — they can
 * get wrapped and the status annotation is lost. This ControllerAdvice
 * guarantees the correct HTTP status is always returned.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EtAuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(EtAuthException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EtBadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(EtBadRequestException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EtResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EtResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        // Log to help debugging
        ex.printStackTrace();
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
