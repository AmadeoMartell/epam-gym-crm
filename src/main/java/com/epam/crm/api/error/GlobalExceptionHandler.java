package com.epam.crm.api.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> build(HttpStatus status, String message, HttpServletRequest req) {
        String tx = MDC.get("transactionId");
        log.warn("err status={} tx={} path={} msg={}", status.value(), tx, req.getRequestURI(), message);
        var body = new LinkedHashMap<String, Object>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("transactionId", tx);
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> conflict(IllegalStateException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> notFound(NoSuchElementException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> unauthorized(SecurityException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> other(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }


}
