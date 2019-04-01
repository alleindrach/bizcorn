package allein.bizcorn.service.security;

import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class CustomAuthenticationLogoutHandler  extends SecurityContextLogoutHandler {
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        super.logout(request,response,authentication);
    }
}
