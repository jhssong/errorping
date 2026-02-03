package com.jhssong.errorping.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String code;
    private String title;
    private String message;
    private HttpStatus status;

    public ResponseEntity<ErrorResponse> toResponseEntity() {
        return ResponseEntity.status(status).body(this);
    }
}
