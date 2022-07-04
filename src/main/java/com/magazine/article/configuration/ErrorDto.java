package com.magazine.article.configuration;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
public class ErrorDto {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorDto(HttpStatus httpStatus, String message, String path) {
        this.timestamp = Instant.now().toEpochMilli();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
    }
}
