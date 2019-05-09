package allein.bizcorn.service.security;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class CustomInvalidSessionStrategy implements org.springframework.security.web.session.InvalidSessionStrategy {
    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        response.sendError(HttpStatus.UNAUTHORIZED.value(),"用户未登录");
    if (!response.isCommitted()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            Result error;
            error=Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
            response.getWriter().print(JSON.toJSONString(error));
            response.getWriter().flush();
        }
    }
}
