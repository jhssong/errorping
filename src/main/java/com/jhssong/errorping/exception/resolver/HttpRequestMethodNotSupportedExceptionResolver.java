package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpRequestMethodNotSupportedExceptionResolver implements ExceptionResolver {

    @Override
    public boolean support(Throwable ex) {
        return ex instanceof HttpRequestMethodNotSupportedException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest request) {
        HttpRequestMethodNotSupportedException e = (HttpRequestMethodNotSupportedException) ex;
        return ErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .title("지원되지 않는 요청 메서드입니다.")
                .message(e.getMessage())
                .build();
    }
}
