package com.projeto.la_couro.api;

/*
 * Tratamento global de erros com payload consistente (timestamp, path, status, error, message, details).
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    public record ErrorResponse(
            OffsetDateTime timestamp,
            String path,
            int status,
            String error,
            String message,
            Object details
    ) {}

    private ResponseEntity<ErrorResponse> build(HttpServletRequest req, HttpStatus status, String message, Object details) {
        var body = new ErrorResponse(
                OffsetDateTime.now(),
                req.getRequestURI(),
                status.value(),
                status.getReasonPhrase(),
                message,
                details
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var fields = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> Map.of("field", f.getField(), "message", Optional.ofNullable(f.getDefaultMessage()).orElse("invalid")))
                .toList();
        return build(req, HttpStatus.BAD_REQUEST, "Validation failed", fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        var items = ex.getConstraintViolations().stream()
                .map(v -> Map.of("property", String.valueOf(v.getPropertyPath()), "message", v.getMessage()))
                .toList();
        return build(req, HttpStatus.BAD_REQUEST, "Constraint violation", items);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, "Missing request parameter", Map.of("parameter", ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        var details = Map.of(
                "name", ex.getName(),
                "requiredType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                "value", Objects.toString(ex.getValue(), "null")
        );
        return build(req, HttpStatus.BAD_REQUEST, "Invalid parameter type", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, "Malformed JSON request", null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return build(req, HttpStatus.CONFLICT, "Data integrity violation", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(req, HttpStatus.FORBIDDEN, "Access denied", null);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        return build(req, HttpStatus.NOT_FOUND, "Resource not found", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest req) {
        return build(req, HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", null);
    }
}
