package allein.bizcorn.service.security;

import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.service.facade.IUserService;
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
import allein.bizcorn.service.security.config.SecurityProperties;
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
            session.setAttribute("username", username);
            session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,
                    exception.getMessage());
        }
        if (exception instanceof BadCredentialsException) {
            User user = userService.getUserByUsername(username).getData();
            userService.incUserLoginErrorTimes(username);
        }
        redirectStrategy.sendRedirect(request, response, securityProperties.getLoginPage() + "?username=" + username);
    }
}