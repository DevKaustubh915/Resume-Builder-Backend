package com.example.resumebuilderapi.exception;


import jdk.javadoc.doclet.Reporter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final View error;

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex){
        Map<String , String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName , errorMessage);
        });

        Map<String , Object> response = new HashMap<>();
        response.put("message" , "Validation failed");
        response.put("errors" , errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceExistException.class)
    public ResponseEntity<Map<String , Object>> handleResourceExistException(ResourceExistException ex){
        Map<String , Object> reponse = new HashMap<>();
        reponse.put("message" , "Resource exists");
        reponse.put("errors" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(reponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String , Object>> handleGenericException(Exception ex){
        Map<String , Object> reponse = new HashMap<>();
        reponse.put("message" , "Something went wrong . contact administator");
        reponse.put("errors" , ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(reponse);
    }
}
