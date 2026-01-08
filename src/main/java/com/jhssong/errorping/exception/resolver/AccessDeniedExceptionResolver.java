package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AccessDeniedExceptionResolver implements ExceptionResolver {
    @Override
    public boolean support(Throwable ex) {
        return ex instanceof AccessDeniedException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest request) {
        AccessDeniedException e = (AccessDeniedException) ex;
        return ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN)
                .title("인가에 실패했습니다.")
                .message(e.getMessage())
                .build();
    }

    @Override
    public LogLevel logLevel() {
        return LogLevel.INFO;
    }

    @Override
    public String logMessage(ErrorResponse er, HttpServletRequest request) {
        return String.format("[AccessDenied] status=%s method=%s uri=%s message=%s",
                er.getStatus(),
                request.getMethod(),
                request.getRequestURI(),
                er.getMessage());
    }
}
