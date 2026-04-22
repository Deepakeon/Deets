package org.example.deets;

import org.example.deets.exceptions.ApiErrorResponse;
import org.example.deets.exceptions.UrlNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ve){
        Map<String, String> errorMap = new HashMap<>();

        ve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMap.put(fieldName, message);
        });

        ApiErrorResponse error = ApiErrorResponse.builder().code("VALIDATION_ERROR").details(errorMap).message(ve.getMessage()).build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUrlNotFoundException(UrlNotFoundException unf) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("error", unf.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder().code("URL_NOT_FOUND").message("Validation failed").details(errorMap).build();

        return ResponseEntity
                .status(unf.getStatusCode())
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(IllegalArgumentException div) {
        ApiErrorResponse error = ApiErrorResponse.builder().code("DUPLICATE_VALUE").message(div.getMessage()).build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}
