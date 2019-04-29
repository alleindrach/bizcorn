package allein.bizcorn.servicefeign.web;


import allein.bizcorn.servicefeign.proxy.UserServiceProxy;
import allein.bizcorn.model.output.Result;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    public Result login(
            @RequestParam String username, @RequestParam String password, @RequestParam(required = false) String captcha) {
//说明： 发起时       从终端 -- cookie1--> Feign -- cookie2-->Service 中，cookie2的复制通过interceptor完成
//      返回时       从Serivice--cookie3-->Feign--cookie4-->终端 的过程中，cookie4的session传播，如果不通过ResponseEntity ，则只能手工添加？存疑
        Result x=userService.login(username,password,captcha);
        if(x.isSuccess())
        {
            String sessionCookieId=(String)x.getData();
            DefaultCookieSerializer cookieSerializer=new DefaultCookieSerializer();
            HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
            cookieSerializer.writeCookieValue(new CookieSerializer.CookieValue(request, response, sessionCookieId));
        }
        return x;
    }

    @PutMapping(value = "/user")
    public Result update(@RequestParam String mobile,HttpServletRequest request,HttpServletResponse response,HttpSession session) {
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
        return userService.update(mobile);
    }
    @RequestMapping(value = "/user/logout")
    public Result logout(HttpServletRequest request,HttpServletResponse response)
    {
        return  userService.logout(request,response);
    }

}
