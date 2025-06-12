package com.example.Library.Security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//Handles validation errors across the entire application

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        //Create a map to store field-specific error messages
        Map<String, String> errors = new HashMap<>();

        //Loop through all fields errors and add them to the map
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        //Return a 404 bad request with the error details in the response body
        return ResponseEntity.badRequest().body(errors);
    }
}
