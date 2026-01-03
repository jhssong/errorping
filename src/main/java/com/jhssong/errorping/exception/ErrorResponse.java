package com.jhssong.errorping.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String code;
    private String title;
    private String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public ResponseEntity<ErrorResponse> toResponseEntity() {
        return ResponseEntity.status(status).body(this);
    }
}
