package allein.bizcorn.service.security;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.model.entity.User;
import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.service.facade.IUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        this.clearAuthenticationAttributes(request);

        if(authentication!=null && authentication.getPrincipal() instanceof UserDetails)
        {
            Result<User> user=userService.getUserByUsername(((UserDetails) authentication.getPrincipal()).getUsername());
            if(user!=null)
                response.getWriter().append(JSON.toJSONString(user)).flush();
        }


//        super.onAuthenticationSuccess(request, response, authentication);
    }
}