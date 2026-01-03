package com.jhssong.errorping;

import com.jhssong.errorping.exception.GlobalExceptionHandler;
import com.jhssong.errorping.exception.resolver.AccessDeniedExceptionResolver;
import com.jhssong.errorping.exception.resolver.AuthenticationExceptionResolver;
import com.jhssong.errorping.exception.resolver.ExceptionResolver;
import com.jhssong.errorping.exception.resolver.HttpRequestMethodNotSupportedExceptionResolver;
import com.jhssong.errorping.exception.resolver.IllegalArgumentExceptionResolver;
import com.jhssong.errorping.exception.resolver.MethodArgumentNotValidExceptionResolver;
import com.jhssong.errorping.exception.resolver.MethodArgumentTypeMismatchExceptionResolver;
import com.jhssong.errorping.exception.resolver.NoResourceFoundExceptionResolver;
import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

@AutoConfiguration
@EnableConfigurationProperties(ErrorpingProperties.class)
public class ErrorpingAutoConfiguration {

    @Bean
    @ConditionalOnClass(AccessDeniedException.class)
    @ConditionalOnMissingBean(AccessDeniedExceptionResolver.class)
    public ExceptionResolver accessDeniedExceptionResolver() {
        return new AccessDeniedExceptionResolver();
    }

    @Bean
    @ConditionalOnClass(AuthenticationException.class)
    @ConditionalOnMissingBean(AuthenticationExceptionResolver.class)
    public ExceptionResolver authenticationExceptionResolver() {
        return new AuthenticationExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean(HttpRequestMethodNotSupportedExceptionResolver.class)
    public ExceptionResolver httpRequestMethodNotSupportedExceptionResolver() {
        return new HttpRequestMethodNotSupportedExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean(IllegalArgumentExceptionResolver.class)
    public ExceptionResolver illegalArgumentExceptionResolver() {
        return new IllegalArgumentExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean(MethodArgumentTypeMismatchExceptionResolver.class)
    public ExceptionResolver methodArgumentTypeMismatchExceptionResolver() {
        return new MethodArgumentTypeMismatchExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean(MethodArgumentNotValidExceptionResolver.class)
    public ExceptionResolver methodArgumentNotValidExceptionResolver() {
        return new MethodArgumentNotValidExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean(NoResourceFoundExceptionResolver.class)
    public ExceptionResolver noResourceFoundExceptionResolver() {
        return new NoResourceFoundExceptionResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorpingService errorpingService(ErrorpingProperties props) {
        return new ErrorpingService(props);
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler(
            List<ExceptionResolver> resolvers, ErrorpingService errorpingService) {
        return new GlobalExceptionHandler(resolvers, errorpingService);
    }

}
