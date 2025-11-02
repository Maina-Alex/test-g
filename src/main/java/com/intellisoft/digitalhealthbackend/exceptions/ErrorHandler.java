package com.intellisoft.digitalhealthbackend.exceptions;

import com.intellisoft.digitalhealthbackend.dto.UniversalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(PatientException.class)
    ResponseEntity<UniversalResponse>handlePatientException(PatientException exception){
        return ResponseEntity.ok(UniversalResponse.builder()
                .status(400)
                .message(exception.getMessage())
                .build());
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<UniversalResponse>handleGenericExceptions(Exception exception){
        return ResponseEntity.ok(UniversalResponse.builder()
                .status(500)
                .message(exception.getMessage())
                .build());
    }
}
