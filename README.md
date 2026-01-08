# Errorping

<div align="center">
<img src="./assets/errorping-logo.png" width="300px"/>
</div>


<!-- ABOUT THE PROJECT -->

## About The Project

**Errorping** is a Spring Boot–oriented error handling library that provides a unified, extensible way to handle
exceptions, generate API error responses, control logging behavior, and optionally trigger alerts.

It is designed to be used as a **library**, while allowing **application-level customization** through simple interface
implementations.

### Built With

- Spring Boot 3.5.4
- JDK 17
- Servlet-based stack (`spring-boot-starter-web`)

### Features

- Centralized global exception handling for Spring Boot
- Pluggable ExceptionResolver architecture
- Consistent API error response format
- Per-exception log level control
- Custom log message formatting
- Optional alert triggering per exception
- No controller-level boilerplate

<!-- GETTING STARTED -->

## Installation

### Option 1: Maven Local (Recommended for development)

In the `errorping` project:

```bash
./gradlew publishToMavenLocal
```

Then, in your application:

```gradle
dependencies {
    implementation "com.jhssong:errorping:0.0.0-SNAPSHOT"
}
```

### Option 2: GitHub Packages (Recommend for production)

Check most recent version at [here](https://github.com/jhssong/errorping/tags)

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/jhssong/errorping")
        credentials {
            username = project.findProperty("gpr.user")
            password = project.findProperty("gpr.key")
        }
    }
}

dependencies {
    implementation "com.jhssong:errorping:0.2.0"
}
```

> GitHub Packages requires authentication even for public repositories.

<!-- CORE CONCEPTS -->

## Core Concepts

1. `ExceptionResolver`

```java
public interface ExceptionResolver {

    boolean support(Throwable ex);

    ErrorResponse resolve(Throwable ex, HttpServletRequest request);

    LogLevel logLevel();

    String logMessage(ErrorResponse er, HttpServletRequest request);

    default boolean shouldAlert(Throwable ex) {
        return false;
    }
}
```

Each resolver is responsible for:

- Determining whether it supports a given exception
- Creating a standardized ErrorResponse
- Defining log level and log message
- Optionally triggering alerts

2. `ErrorResponse`

```json
{
  "timestamp": "2026-01-08T17:30:43.257027",
  "status": "BAD_REQUEST",
  "title": "잘못된 요청입니다.",
  "message": "부적절한 이름입니다."
}
```

### Creating a Custom Exception Resolver

In your project, simply implement the interface:

```java

@Component
public class CustomExceptionResolver implements ExceptionResolver {

    @Override
    public boolean support(Throwable ex) {
        return ex instanceof CustomException;
    }

    @Override
    public ErrorResponse resolve(Throwable ex, HttpServletRequest httpServletRequest) {
        CustomException e = (CustomException) ex;
        return ErrorResponse.builder()
                .status(e.getStatus())
                .title(e.getTitle())
                .message(e.getMessage())
                .build();
    }

    @Override
    public LogLevel logLevel() {
        return LogLevel.ERROR;
    }

    @Override
    public String logMessage(ErrorResponse er, HttpServletRequest request) {
        return String.format("[CustomException] status=%s method=%s uri=%s message=%s",
                er.getStatus(),
                request.getMethod(),
                request.getRequestURI(),
                er.getMessage());
    }

    @Override
    public boolean shouldAlert(Throwable ex) {
        CustomException e = (CustomException) ex;
        return e.isShouldAlert();
    }
}
```

- Automatically detected by Errorping’s global exception handler
- No additional configuration required

## Configuration

Errorping uses Spring Boot `@ConfigurationProperties` to load alert-related settings.
To enable Discord alerting, add the following to your `application.yml`:

```yaml
errorping:
  discord-webhook-url: ${DISCORD_WEBHOOK_URL}
```

### Alert Triggering Rules

Discord alerts are sent **only when both conditions are met**:

1. The corresponding `ExceptionResolver` returns `true` from `shouldAlert(Throwable ex)`
2. `errorping.discord-webhook-url` is configured

This allows full control over which exceptions should trigger external alerts.

### How It Works Internally

1. An exception is thrown in a controller
2. ErrorPing’s GlobalExceptionHandler intercepts it
3. All registered ExceptionResolvers are scanned (ordered)
4. The first resolver that support()s the exception is used
5. Error response is returned
6. Log is written using the resolver’s log level & message
7. Alert is triggered if shouldAlert() is true

> If multiple resolvers support the same exception,
> they are evaluated in order using Spring's `@Order` or `Ordered` interface.

### Example

For more examples, check [errorping-example](https://github.com/jhssong/errorping-example)