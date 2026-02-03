package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodArgumentTypeMismatchExceptionResolver implements ExceptionResolver {

    @Override
    public boolean support(Throwable ex) {
        return ex instanceof MethodArgumentTypeMismatchException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest request) {
        MethodArgumentTypeMismatchException e = (MethodArgumentTypeMismatchException) ex;

        String paramName = e.getName();
        String requiredType = e.getRequiredType() != null
                ? e.getRequiredType().getSimpleName()
                : "unknown";
        String message = String.format("'%s' 파라미터는 %s 타입이어야 합니다.", paramName, requiredType);

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("인자 타입이 일치하지 않습니다.")
                .message(message)
                .build();
    }

    @Override
    public LogLevel logLevel() {
        return LogLevel.INFO;
    }
}
