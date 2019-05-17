package allein.bizcorn.service.exception;


import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
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

    // Convert a predefined exception to an HTTP Status code
    @ResponseStatus(value = HttpStatus.GONE,
            reason = "资源不存在")  // 410
    @org.springframework.web.bind.annotation.ExceptionHandler(FileNotFoundException.class)
    public Result fileNotFound(HttpServletRequest req, FileNotFoundException ex) {
        logger.error("Request: " + req.getRequestURL(), ex);
        return Result.failWithException(ex);
    }
//用户未登录
    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public Result fileNotFound(HttpServletRequest req, AuthenticationCredentialsNotFoundException ex) throws IOException {
        logger.error("Request: " + req.getRequestURL(), ex);

        return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
    }

//    @ResponseStatus(value = HttpStatus.FORBIDDEN,
//            reason = "无权")  // 403
    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result notAuthoried(HttpServletRequest req, AccessDeniedException ex) {
        logger.error("Request: " + req.getRequestURL(), ex);
        return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
    }
    // Specify name of a specific view that will be used to display the error:
    @org.springframework.web.bind.annotation.ExceptionHandler({SQLException.class, DataAccessException.class})
    public Result databaseError(HttpServletRequest req, Exception ex) {

        logger.error("Request: " + req.getRequestURL(), ex);

        return new Result(ex);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(CommonException.class)
    public @ResponseBody
    Result handleCommonError(HttpServletRequest req, CommonException ex) {
        logger.error("Request: " + req.getRequestURL(), ex);
        return Result.failWithException(ex);
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public @ResponseBody
    Result handleError(HttpServletRequest req, Exception ex) {
        logger.error("Request: " + req.getRequestURL(), ex);
        return  Result.failWithException(new CommonException(ExceptionEnum.LOST_CONNECTION_TO_SERVER));
    }



}
