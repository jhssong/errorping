package com.jhssong.errorping.exception;

import com.jhssong.errorping.ErrorpingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Modify according to your project's specific needs.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorpingService errorpingService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                         HttpServletRequest request) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.info("[400] Validation Failed: {}", message);
        errorpingService.sendErrorToDiscord(e, HttpStatus.BAD_REQUEST, request, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Void> handleMethodArgumentTypeMismatchException(
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        String requiredType = (e.getRequiredType() != null) ? e.getRequiredType().getSimpleName() : "unknown";
        log.info("[400] Type Mismatch: field '{}' expected type '{}'", e.getName(), requiredType);
        errorpingService.sendErrorToDiscord(e, HttpStatus.BAD_REQUEST, request,
                String.format("field '%s' expected type '%s'", e.getName(), requiredType));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException e,
                                                                  HttpServletRequest request) {
        log.warn("[400] Illegal Argument: {}", e.getMessage());
        errorpingService.sendErrorToDiscord(e, HttpStatus.NOT_FOUND, request, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    protected ResponseEntity<Void> handleServletRequestBindingException(
            ServletRequestBindingException e, HttpServletRequest request) {
        log.debug("[400] Invalid Access: {}", e.getMessage());
        errorpingService.sendErrorToDiscord(e, HttpStatus.BAD_REQUEST, request, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.debug("[405] Method Not Allowed: {}", e.getMessage());
        errorpingService.sendErrorToDiscord(e, HttpStatus.METHOD_NOT_ALLOWED, request, e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException e,
                                                                  HttpServletRequest request) {
        log.debug("[404] No Resource Found: {}", e.getMessage());
        errorpingService.sendErrorToDiscord(e, HttpStatus.NOT_FOUND, request, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("[500] Internal Server Error", e);
        errorpingService.sendErrorToDiscord(e, HttpStatus.INTERNAL_SERVER_ERROR, request, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

