package com.github.manimovassagh.uploader.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("File not found: " + ex.getMessage());
    }

    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<String> handleEmptyFileException(EmptyFileException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Failed to store empty file: " + ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("File size exceeds maximum limit");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred: " + ex.getMessage());
    }
} 