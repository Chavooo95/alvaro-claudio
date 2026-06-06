package com.boardgames.catalog.infrastructure.rest;

import com.boardgames.catalog.domain.GameNotFoundException;
import com.boardgames.catalog.domain.InvalidGameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(assignableTypes = GameController.class)
class CatalogExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleNotFound(GameNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    @ExceptionHandler(InvalidGameException.class)
    ResponseEntity<Map<String, Object>> handleInvalid(InvalidGameException ex) {
        return problem(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    private ResponseEntity<Map<String, Object>> problem(HttpStatus status, String message, List<String> errors) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "errors", errors
        ));
    }
}
