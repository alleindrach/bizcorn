package allein.bizcorn.service.exception;


import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.model.output.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

@ControllerAdvice
public class ExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    // Convert a predefined exception to an HTTP Status code
    @ResponseStatus(value = HttpStatus.CONFLICT,
            reason = "Data integrity violation")  // 409
    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    public Result conflict(HttpServletRequest req, DataIntegrityViolationException ex) {
        logger.error("Request: " + req.getRequestURL(), ex);

        return new Result(ex);
    }

    // Specify name of a specific view that will be used to display the error:
    @org.springframework.web.bind.annotation.ExceptionHandler({SQLException.class, DataAccessException.class})
    public Result databaseError(HttpServletRequest req, Exception ex) {
        // Nothing to do.  Returns the logical view name of an error page, passed
        // to the view-resolver(s) in usual way.
        // Note that the exception is NOT available to this view (it is not added
        // to the model) but see "Extending ExceptionHandlerExceptionResolver"
        // below.
        logger.error("Request: " + req.getRequestURL(), ex);

        return new Result(ex);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(CommonException.class)
    public @ResponseBody
    Result<Object> handleCommonError(HttpServletRequest req, CommonException ex) {
        logger.error("Request: " + req.getRequestURL(), ex);

        return new Result<Object>(ex);
    }

    // Total control - setup a model and return the view name yourself. Or
    // consider subclassing ExceptionHandlerExceptionResolver (see below).
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public @ResponseBody
    Result<Object> handleError(HttpServletRequest req, Exception ex) {
        logger.error("Request: " + req.getRequestURL(), ex);

        return new Result<Object>(ex);
    }


}
