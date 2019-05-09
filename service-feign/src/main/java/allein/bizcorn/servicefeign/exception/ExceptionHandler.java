/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.servicefeign.exception;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import com.netflix.hystrix.exception.HystrixBadRequestException;
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
import java.sql.SQLException;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-08 15:59
**/
@ControllerAdvice
public class ExceptionHandler {

    public static final String DEFAULT_ERROR_VIEW = "error";
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    // Convert a predefined exception to an HTTP Status code

    @org.springframework.web.bind.annotation.ExceptionHandler(HystrixBadRequestException.class)
    public Result hystrixFall(HttpServletRequest req, HystrixBadRequestException ex) throws IOException {
        logger.error("Request: " + req.getRequestURL(), ex);
        if(ex.getCause() instanceof FileNotFoundException)
        {
            HttpServletResponse response =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.sendError(HttpStatus.GONE.value(),ex.getMessage());
            return null;
        }else
        if(ex.getCause() instanceof AccessDeniedException)
        {
//            HttpServletResponse response =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//            response.sendError(HttpStatus.FORBIDDEN.value(),ex.getMessage());
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
        }else
        if(ex.getCause() instanceof AuthenticationCredentialsNotFoundException)
        {
//            HttpServletResponse response =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//            response.sendError(HttpStatus.UNAUTHORIZED.value(),ex.getMessage());
            return Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
        }
        return null;
    }
    // Total control - setup a model and return the view name yourself. Or
    // consider subclassing ExceptionHandlerExceptionResolver (see below).
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,
            reason = "")  // 409
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public @ResponseBody
    Result<Object> handleError(HttpServletRequest req, Exception ex) {
        return null;
    }



}

