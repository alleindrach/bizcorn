package allein.bizcorn.service.security;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalNotFoundException;

public class AuthorizationFailure implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        throw new AccessDeniedException("");

        if (!response.isCommitted()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            Result error;
            error=Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_AUHTORIZED));
            response.getWriter().print(JSON.toJSONString(error));
            response.getWriter().flush();
        }
    }
}
