package com.fund.app.box.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EventAlreadyExistsException.class)
    public ResponseEntity<?> handleEventAlreadyExistsException(EventAlreadyExistsException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return new ResponseEntity<>(errorMap, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFormat(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (mostSpecificCause instanceof InvalidFormatException invalidFormat) {
            String fieldName = invalidFormat.getPath().getFirst().getFieldName();
            String invalidValue = invalidFormat.getValue().toString();
            errors.put(fieldName, "Invalid value '" + invalidValue + "'. Allowed values: EUR, USD, GBP.");
        } else {
            errors.put("error", "Malformed JSON or invalid value.");
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
