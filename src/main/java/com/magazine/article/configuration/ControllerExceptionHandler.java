package com.magazine.article.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorDto> handleBadRequest(HttpServletRequest request, Exception e) {
        ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
        log.error(error.toString());
        return ResponseEntity.badRequest()
                             .body(error);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message;
        if (fieldError != null) {
            message = String.format("Error in %s object, field %s: %s",
                                    fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
        } else {
            message = "Object is not valid";
        }
        ErrorDto error = new ErrorDto(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
        log.error(error.toString());
        return ResponseEntity.badRequest()
                             .body(error);
    }
}
