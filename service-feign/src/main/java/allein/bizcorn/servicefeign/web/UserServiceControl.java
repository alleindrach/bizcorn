package allein.bizcorn.servicefeign.web;


import allein.bizcorn.servicefeign.proxy.UserServiceProxy;
import allein.bizcorn.model.output.Result;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@RestController
@RefreshScope
public class UserServiceControl {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceControl.class);


    @Autowired
    UserServiceProxy userService;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @RequestMapping(value = "/user/login")
    @ResponseBody
    public Result login(
            RequestTemplate requestTemplate,
            @RequestParam String username, @RequestParam String password, @RequestParam String captcha, HttpServletRequest request, HttpSession session) {
        String sessionId =  RequestContextHolder.getRequestAttributes().getSessionId();

        logger.info(" {} >>> {}",  request.getRequestURL().toString(),sessionId);
        Enumeration e = request.getSession().getAttributeNames();
        while (e.hasMoreElements()) {
            String ename = (String) e.nextElement();
            logger.info("Session >>>>> {} => {}",ename,request.getSession().getAttribute(ename));
        }
        Cookie[] cookies =request.getCookies();
        if(cookies!=null) {
            for (int i = 0; i < cookies.length; i++) {
                logger.info("Cookie >>>>> {} => {}", cookies[i].getName(), cookies[i].getValue());
//                requestTemplate.header("Cookie", cookies[i].getName()+"=" + cookies[i].getValue());
            }
        }


        Result x=userService.login(username,password,captcha);
        return x;
    }

    @PutMapping(value = "/user")
    @ResponseBody
    public Result update(@RequestParam String mobile,HttpServletRequest request,HttpServletResponse response,HttpSession session) {
        Object uo=session.getAttribute(sessionAttrUser);
        return userService.update(mobile,session,request);
    }
    @RequestMapping(value = "/user/logout")
    @ResponseBody
    public Result logout(HttpServletRequest request,HttpServletResponse response)
    {
        return  userService.logout(request,response);
    }

}
