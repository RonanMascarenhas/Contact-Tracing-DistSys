package ie.sds.resultsDiscovery.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import service.exception.NoSuchServiceException;

/**
 * Class holds procedures for dealing with {@code ResultsDiscoveryController} errors.
 */
@RestControllerAdvice
public class RDControllerAdvice {
    private static final Integer retryAfterSeconds = 30;

    @ExceptionHandler(NoSuchServiceException.class)
    public ResponseEntity<?> handleServiceUnavailable() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("Retry-After", retryAfterSeconds.toString())
                .build();
    }

    @ExceptionHandler(JmsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleJmsUnavailable() {
    }

    @ExceptionHandler(RestClientException.class)
    public void handleRestException() {
    }
}
