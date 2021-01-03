package ie.ucd.sds.webUI.controller.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import service.exception.InvalidEntityException;
import service.exception.NoSuchServiceException;

@ControllerAdvice
public class GlobalControllerAdvice {
    // todo implement correct handling

    @ExceptionHandler(NoSuchServiceException.class)
    public String handleMissingService(Model model) {
        model.addAttribute("msg", "A back-end service is offline. Please wait and try again.");
        return "/problem";
    }

    @ExceptionHandler(InvalidEntityException.class)
    public void handleMalformedEntity() {
    }
}
