package allein.bizcorn.service.security;

import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Component
public class CustomAuthenticationLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
        boolean isAjax = true;//"XMLHttpRequest".equals(ajaxHeader);
        if (isAjax) {
            try {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                response.getWriter().print(JSON.toJSONString(Result.successWithMessage("logout!")));
                response.getWriter().flush();
            }catch(Exception ex)
            {

            }
        }else
        {
            super.handle(request, response, authentication);
        }
    }
}
