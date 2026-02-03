package com.jhssong.errorping.exception.resolver;

import com.jhssong.errorping.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodArgumentNotValidExceptionResolver implements ExceptionResolver {

    private static final Map<String, BiFunction<String, FieldError, String>> MESSAGE_GENERATORS =
            Map.of(
                    "NotBlank", (field, error) -> field + " 필드는 필수입니다.",
                    "NotNull", (field, error) -> field + " 필드는 필수입니다.",
                    "Email", (field, error) -> field + " 형식이 올바르지 않습니다."
            );

    private static String extractConstraint(FieldError error) {
        String[] codes = error.getCodes();
        return (codes != null && codes.length > 0)
                ? codes[codes.length - 1]
                : "";
    }

    private boolean hasCustomMessage(String message) {
        if (message == null) {
            return false;
        }

        return !message.startsWith("{")
                && !message.equals("must not be blank")
                && !message.equals("must be a well-formed email address");
    }


    @Override
    public boolean support(Throwable ex) {
        return ex instanceof MethodArgumentNotValidException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest request) {
        MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;

        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String field = error.getField();
                    String constraint = extractConstraint(error);

                    String defaultMessage = error.getDefaultMessage();
                    if (hasCustomMessage(defaultMessage)) {
                        return defaultMessage;
                    }

                    return MESSAGE_GENERATORS
                            .getOrDefault(constraint,
                                    (f, err) -> f + ": " + err.getDefaultMessage()
                            )
                            .apply(field, error);
                })
                .distinct()
                .collect(Collectors.joining(", "));

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .title("유효성 검사에 실패했습니다.")
                .message(message)
                .build();
    }

    @Override
    public LogLevel logLevel() {
        return LogLevel.INFO;
    }
}
