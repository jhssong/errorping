package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface ExceptionResolver {

    boolean support(Throwable ex);

    ErrorResponse resolve(Throwable ex, HttpServletRequest request);

    default boolean shouldAlert() {
        return false;
    }
}
