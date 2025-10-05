package com.postech.reservationservice.api.exception;

import com.postech.core.common.exception.BusinessException;
import com.postech.core.common.exception.HttpStatusResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleAccessDeniedException(
            ServerWebExchange exchange,
            AccessDeniedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail("Access to the resource is prohibited");
        problemDetail.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleWebExchangeBindException(
            ServerWebExchange exchange,
            WebExchangeBindException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setDetail("Validation failed for request body");
        problemDetail.setInstance(URI.create(exchange.getRequest().getPath().value()));
        Map<String, String> errors = new LinkedHashMap<>();
        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        problemDetail.setProperty("errors", errors);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail));
    }

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleBusinessException(
            ServerWebExchange exchange,
            BusinessException exception) {
        int httpStatus = HttpStatusResolver.resolveHttpStatusForBusinessException(exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle("Business Exception");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return Mono.just(ResponseEntity.status(httpStatus).body(problemDetail));
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleGenericException(
            ServerWebExchange exchange,
            Exception exception) {
        log.error("An unexpected error occurred: {}", exception.getMessage(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected error occurred. Please try again later");
        problemDetail.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail));
    }
}
