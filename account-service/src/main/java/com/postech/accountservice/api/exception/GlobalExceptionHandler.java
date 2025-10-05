package com.postech.accountservice.api.exception;

import com.postech.core.common.exception.BusinessException;
import com.postech.core.common.exception.HttpStatusResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(
            HttpServletRequest request,
            AccessDeniedException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail("Access to the resource is prohibited");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setDetail("Validation failed for request body");
        Map<String, String> errors = new LinkedHashMap<>();
        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        problemDetail.setProperty("errors", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problemDetail);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusinessException(BusinessException exception) {
        int httpStatus = HttpStatusResolver.resolveHttpStatusForBusinessException(exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setTitle("Business Exception");
        problemDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(httpStatus).body(problemDetail);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ProblemDetail> handleHttpClientErrorException(HttpClientErrorException exception) {
        ProblemDetail clientProblemDetail = exception.getResponseBodyAs(ProblemDetail.class);
        if (clientProblemDetail == null) {
            return this.handleGenericException(exception);
        }
        return ResponseEntity.status(exception.getStatusCode()).body(clientProblemDetail);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception exception) {
        log.error("An unexpected error occurred: {}", exception.getMessage(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("An unexpected error occurred. Please try again later");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
