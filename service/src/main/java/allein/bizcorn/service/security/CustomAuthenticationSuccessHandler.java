package allein.bizcorn.service.security;

import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.IUserService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        this.clearAuthenticationAttributes(request);

        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
        if (isAjax) {

            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            Result<IUser> user=Result.successWithData( ((UserDetails) authentication.getPrincipal()).getUsername());
            response.getWriter().print(JSON.toJSONString(user));
            response.getWriter().flush();
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}