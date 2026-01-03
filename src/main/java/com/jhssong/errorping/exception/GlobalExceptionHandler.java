package com.jhssong.errorping.exception;

import com.jhssong.errorping.ErrorpingService;
import com.jhssong.errorping.exception.resolver.ExceptionResolver;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex, HttpServletRequest request
    ) {
        for (ExceptionResolver resolver : resolvers) {
            // 이 Resolver가 처리 가능한 예외인지 확인
            if (!resolver.support(ex)) {
                continue;
            }
            ErrorResponse errorResponse = resolver.resolve(ex, request);

            // Discord 알림을 보낼지 여부
            if (resolver.shouldAlert()) {
                errorpingService.sendErrorToDiscord(errorResponse, request);
            }

            return errorResponse.toResponseEntity();
        }

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Internal Server Error")
                .message("알 수 없는 에러가 발생했습니다.")
                .build();

        return response.toResponseEntity();
    }
}
