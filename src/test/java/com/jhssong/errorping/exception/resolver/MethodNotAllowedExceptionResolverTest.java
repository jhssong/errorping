//package com.jhssong.errorping.exception.resolver;
//
//import com.jhssong.errorping.exception.ErrorResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DisplayName("MethodNotAllowedExceptionResolver 테스트")
//class MethodNotAllowedExceptionResolverTest {
//
//    private MethodNotAllowedExceptionResolver resolver;
//    private MockHttpServletRequest request;
//
//    @BeforeEach
//    void setUp() {
//        resolver = new MethodNotAllowedExceptionResolver();
//        request = new MockHttpServletRequest();
//    }
//
//    @Test
//    @DisplayName("예외를 올바르게 처리하고 METHOD_NOT_ALLOWED 응답 반환")
//    void resolve_ValidException_ReturnsMethodNotAllowedResponse() {
//        // given
//        String[] methods = {"POST", "PUT", "DELETE", "PATCH"};
//
//        for (String method : methods) {
//            HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException(method);
//            request.setRequestURI("/api/test");
//
//            // when
//            ErrorResponse response = resolver.resolve(exception, request);
//
//            // then
//            assertThat(response).isNotNull();
//            assertThat(response.getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());
//            assertThat(response.getTitle()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
//            assertThat(response.getMessage()).contains(method);
//        }
//    }
//
//    @Test
//    @DisplayName("supports 메서드 동작 확인")
//    void support_InheritedMethod_WorksCorrectly() {
//        // given
//        HttpRequestMethodNotSupportedException validException = new HttpRequestMethodNotSupportedException("GET");
//        RuntimeException invalidException = new RuntimeException();
//
//        // when & then
//        assertThat(resolver.support(validException)).isTrue();
//        assertThat(resolver.support(invalidException)).isFalse();
//    }
//}