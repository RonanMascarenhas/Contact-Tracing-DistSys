package ie.ucd.sds.webUI.controller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import service.exception.InvalidEntityException;
import service.exception.NoSuchServiceException;

@ControllerAdvice
public class ResultsDiscoveryControllerAdvice {
    // todo response statuses here?
    // todo implement correct handling

    @ExceptionHandler(NoSuchServiceException.class)
    public void handleMissingService() {
    }

    @ExceptionHandler(InvalidEntityException.class)
    public void handleMalformedEntity() {
    }
}
