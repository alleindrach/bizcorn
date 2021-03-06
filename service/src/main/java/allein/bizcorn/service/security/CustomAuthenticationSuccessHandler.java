package allein.bizcorn.service.security;

import allein.bizcorn.common.mq.Topic;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.model.output.Result;
import allein.bizcorn.service.facade.IMessageQueueService;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.implement.UserServiceMongoImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private IMessageQueueService messageQueueService;
    private String base64Encode(String value) {
        byte[] encodedCookieBytes = Base64.getEncoder().encode(value.getBytes());
        return new String(encodedCookieBytes);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = request.getParameter("username");
        User user = userService.getUser(username);
        if (user!=null)
            userService.rstUserLoginErrorTimes(user.getUsername());
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.setAttribute("username", username);
//            logger.info("Auth session>>>>{},{}",session.getId(),base64Encode(session.getId()));
//        }
        this.clearAuthenticationAttributes(request);

        String ajaxHeader = ((HttpServletRequest) request).getHeader("X-Requested-With");
        boolean isAjax =true;// "XMLHttpRequest".equals(ajaxHeader);
        if (isAjax) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            user= userService.getUser( ((UserDetails) authentication.getPrincipal()).getUsername());
            SerializeConfig config=new SerializeConfig();
            config.put(user.getClass(),new User.FullSerializer());
            JSONObject jsonUser= (JSONObject) JSON.parse( JSON.toJSONString(user,config));
            response.getWriter().print(JSON.toJSONString(Result.successWithData(jsonUser)));
//            Result<String> sessionIdCookie=Result.successWithData(session.getId());
//            response.getWriter().print(JSON.toJSONString(sessionIdCookie));
            response.getWriter().flush();
            messageQueueService.send(Topic.USER_LOGIN,"user login success!");
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}