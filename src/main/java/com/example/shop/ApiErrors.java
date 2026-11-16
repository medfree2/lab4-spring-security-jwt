package com.example.shop;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail conflict() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Opération incompatible avec les relations existantes.");
    }

    @ExceptionHandler(PropertyReferenceException.class)
    ProblemDetail invalidSort() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Champ de tri inconnu.");
    }
}
