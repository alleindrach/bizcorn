/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.servicefeign.config;

import allein.bizcorn.common.exception.FileNotExistException;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import feign.Response;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-08 15:33
 **/
public class BizExceptionFeignErrorDecoder implements feign.codec.ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(BizExceptionFeignErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.SC_GONE) {
            return new HystrixBadRequestException( String.valueOf(response.status()),new FileNotFoundException());
        }else
        if (response.status() == HttpStatus.SC_FORBIDDEN) {
            return new HystrixBadRequestException( String.valueOf(response.status()),new AccessDeniedException("用户权限不足"));
        }else
        if (response.status() == HttpStatus.SC_UNAUTHORIZED) {
            return new HystrixBadRequestException( String.valueOf(response.status()),
                    new AuthenticationCredentialsNotFoundException("用户未登录")
            );
        }

        return feign.FeignException.errorStatus(methodKey, response);
    }

}