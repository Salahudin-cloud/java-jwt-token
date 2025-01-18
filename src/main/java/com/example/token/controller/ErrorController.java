package com.example.token.controller;

import com.example.token.exception.AuthenticationExeception;
import com.example.token.model.WebResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorController {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<String>> constrainViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<String>builder().message(exception.getMessage()).build());
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>>responseStatusException(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(WebResponse.<String>builder().message(exception.getReason()).build());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<WebResponse<String>> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WebResponse.<String>builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(AuthenticationExeception.class)
    public ResponseEntity<WebResponse<String>> handleAuthenticationException(AuthenticationExeception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(WebResponse.<String>builder().message(e.getMessage()).build());
    }

}
