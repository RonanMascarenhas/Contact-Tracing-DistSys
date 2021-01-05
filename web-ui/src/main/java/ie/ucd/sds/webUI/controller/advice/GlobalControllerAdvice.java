package ie.ucd.sds.webUI.controller.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import service.exception.InvalidEntityException;
import service.exception.NoSuchServiceException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(NoSuchServiceException.class)
    public String handleMissingService(NoSuchServiceException ex, Model model) {
        model.addAttribute("msg", ex.getMessage());
        return "/problem";
    }

    @ExceptionHandler(InvalidEntityException.class)
    public String handleMalformedEntity(InvalidEntityException ex, Model model) {
        model.addAttribute("msg", ex.getMessage());
        return "/problem";
    }
}
