package ie.sds.resultsDiscovery.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import service.exception.NoSuchServiceException;

@RestControllerAdvice
public class DnsErrorControllerAdvice {
    private static final Integer retryAfterSeconds = 30;

    @ExceptionHandler(NoSuchServiceException.class)
    public ResponseEntity<?> handleServiceUnavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", retryAfterSeconds.toString())
                .build();
    }
}
