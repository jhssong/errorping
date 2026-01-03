package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class NoResourceFoundExceptionResolver implements ExceptionResolver {
    @Override
    public boolean support(Throwable ex) {
        return ex instanceof NoResourceFoundException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest request) {
        NoResourceFoundException e = (NoResourceFoundException) ex;
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .title("요청한 리소스를 찾을 수 없습니다.")
                .message(e.getMessage())
                .build();
    }
}
