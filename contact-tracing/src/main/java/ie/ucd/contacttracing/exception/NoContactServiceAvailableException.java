package ie.ucd.contacttracing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FAILED_DEPENDENCY)
public class NoContactServiceAvailableException extends RuntimeException{

}
