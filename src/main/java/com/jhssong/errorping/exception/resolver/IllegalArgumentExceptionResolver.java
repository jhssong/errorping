package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class IllegalArgumentExceptionResolver implements ExceptionResolver {

    @Override
    public boolean support(Throwable ex) {
        return ex instanceof IllegalArgumentException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest request) {
        IllegalArgumentException e = (IllegalArgumentException) ex;
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("잘못된 요청입니다.")
                .message(e.getMessage())
                .build();
    }

    @Override
    public LogLevel logLevel() {
        return LogLevel.INFO;
    }
}
