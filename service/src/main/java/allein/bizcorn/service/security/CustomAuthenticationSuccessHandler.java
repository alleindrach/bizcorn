package allein.bizcorn.service.security;

import allein.bizcorn.service.facade.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private IUserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = request.getParameter("username");
        userService.rstUserLoginErrorTimes(username);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("username", username);
        }
        String originalUrl=request.getParameter("originalUrl");
        if(originalUrl!=null && !originalUrl.isEmpty())
            super.getRedirectStrategy().sendRedirect(request, response,  originalUrl);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}