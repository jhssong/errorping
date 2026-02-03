package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationExceptionResolver implements ExceptionResolver {

    @Override
    public boolean support(Throwable ex) {
        return ex instanceof AuthenticationException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest request) {
        AuthenticationException e = (AuthenticationException) ex;
        return ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .title("인증에 실패했습니다.")
                .message(e.getMessage())
                .build();
    }

    @Override
    public LogLevel logLevel() {
        return LogLevel.WARN;
    }
}
