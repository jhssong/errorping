package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.logging.LogLevel;

public interface ExceptionResolver {

    boolean support(Throwable ex);

    ErrorResponse resolve(Throwable ex, HttpServletRequest request);

    LogLevel logLevel();

    String logMessage(ErrorResponse er, HttpServletRequest request);

    default boolean shouldAlert(Throwable ex) {
        return false;
    }
}
