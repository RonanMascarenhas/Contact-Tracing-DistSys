package ie.sds.resultsDiscovery.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.jms.JmsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Class to define handling procedure for {@code JmsExceptions}, mainly with respect to empty Jms Queues.
 */
@RestControllerAdvice
public class JmsErrorControllerAdvice {
    @ExceptionHandler(JmsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleServiceUnavailable() {
    }
}
