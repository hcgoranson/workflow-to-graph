package one.goranson.wf2graph.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception exception) {

        final ModelAndView mav = new ModelAndView();
        if (exception instanceof ValidationException) {
            mav.addObject("validation", exception.getMessage());
            if (exception.getCause() != null ) {
                mav.addObject("error", exception.getCause().getMessage());
            }
            mav.setViewName("upload");
            return mav;
        }

        try {
                mav.addObject("type", exception.getClass().toString());
                mav.addObject("message", exception.getMessage());
                mav.setViewName("error");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mav;
    }
}