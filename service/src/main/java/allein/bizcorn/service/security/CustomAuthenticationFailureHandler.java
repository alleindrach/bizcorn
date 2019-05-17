package allein.bizcorn.service.security;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.IUserService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private allein.bizcorn.service.security.config.SecurityProperties securityProperties;
    @Autowired
    private IUserService userService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.removeAttribute("username");
            session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,
                    exception.getMessage());
        }
        if (exception instanceof BadCredentialsException) {
            User user = userService.getUser(username);
            userService.incUserLoginErrorTimes(username);
        }
        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
        boolean isAjax =true;// "XMLHttpRequest".equals(ajaxHeader);
        if (isAjax) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            Result error;
            if(exception instanceof  BadCredentialsException){
                error=Result.failWithException(new CommonException(ExceptionEnum.USER_ACCOUNT_LOGIN_FAIL));
            }
            else if("login.captcha.error".compareToIgnoreCase(exception.getMessage())==0)
            {
                error=Result.failWithException(new CommonException(ExceptionEnum.USER_CAPTCHA_ERROR));
            }else // if("login.username-or-password.error".compareToIgnoreCase(exception.getMessage())==0)
            {
                error=Result.failWithException(new CommonException(ExceptionEnum.USER_NOT_LOGIN));
            }
            response.getWriter().print(JSON.toJSONString(error));
            response.getWriter().flush();
        } else {
            redirectStrategy.sendRedirect(request, response, securityProperties.getLoginPage() + "?username=" + username);
        }
    }
}