package allein.servicefeign.web;

import allein.model.output.Result;
import allein.servicefeign.interceptor.SessionInterceptor;
import allein.servicefeign.service.ServiceGate;
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
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@RestController
@RefreshScope
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    ServiceGate serviceGate;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @RequestMapping(value = "/login")
    @ResponseBody
    public Result login(
            RequestTemplate requestTemplate,
            @RequestParam String name, @RequestParam String password, HttpServletRequest request, HttpSession session) {
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


        Result x=serviceGate.login(name,password);
        return x;
    }

    @PutMapping(value = "/")
    @ResponseBody
    public Result update(@RequestParam String mobile,HttpSession session) {
        Object uo=session.getAttribute(sessionAttrUser);


        return serviceGate.update(mobile);
    }

}
