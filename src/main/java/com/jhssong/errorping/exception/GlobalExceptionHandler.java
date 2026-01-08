package com.jhssong.errorping.exception;

import com.jhssong.errorping.ErrorpingService;
import com.jhssong.errorping.exception.resolver.ExceptionResolver;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final List<ExceptionResolver> resolvers;
    private final ErrorpingService errorpingService;

    public GlobalExceptionHandler(List<ExceptionResolver> resolvers, ErrorpingService errorpingService) {
        // @Order를 기준으로 정렬 (낮을수록 우선)
        this.resolvers = resolvers.stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .toList();
        this.errorpingService = errorpingService;
    }

    private void log(
            LogLevel level,
            String message,
            Throwable ex
    ) {
        switch (level) {
            case TRACE -> log.trace(message);
            case DEBUG -> log.debug(message);
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
            case ERROR -> log.error(message);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex, HttpServletRequest request
    ) {
        for (ExceptionResolver resolver : resolvers) {
            // 이 Resolver가 처리 가능한 예외인지 확인
            if (!resolver.support(ex)) {
                continue;
            }
            ErrorResponse errorResponse = resolver.resolve(ex, request);

            // log 메세지 출력
            LogLevel level = resolver.logLevel();
            String message = resolver.logMessage(errorResponse, request);
            log(level, message, ex);

            if (resolver.shouldAlert(ex)) {
                errorpingService.sendErrorToDiscord(errorResponse, request);
            }

            return errorResponse.toResponseEntity();
        }

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .title("Internal Server Error")
                .message("알 수 없는 에러가 발생했습니다.")
                .build();

        return response.toResponseEntity();
    }
}
