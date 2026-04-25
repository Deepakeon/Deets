package org.example.deets;

import lombok.extern.slf4j.Slf4j;
import org.example.deets.exceptions.ApiErrorResponse;
import org.example.deets.exceptions.UrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ve){
        log.error("Validation failed: {} errors in request", ve.getBindingResult().getErrorCount(), ve);
        Map<String, String> errorMap = new HashMap<>();

        ve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errorMap.put(fieldName, message);
        });
        log.error("Validation errors: {}", errorMap);

        ApiErrorResponse error = ApiErrorResponse.builder().code("VALIDATION_ERROR").details(errorMap).message("Validation error").build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUrlNotFoundException(UrlNotFoundException unf) {
        log.error("Url not found: {}", unf.getMessage());
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("error", unf.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder().code("URL_NOT_FOUND").message("Validation failed").details(errorMap).build();

        return ResponseEntity
                .status(unf.getStatusCode())
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(IllegalArgumentException ia) {
        log.error("Duplicate value error: {}", ia.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder().code("DUPLICATE_VALUE").message(ia.getMessage()).build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}
