package allein.bizcorn.servicefeign.web;


import allein.bizcorn.common.config.SecurityConstants;
import allein.bizcorn.model.facade.IUser;
import allein.bizcorn.model.mongo.User;
import allein.bizcorn.service.facade.gate.IUserServiceGate;
import allein.bizcorn.servicefeign.proxy.UserServiceProxy;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@RestController
@RefreshScope
public class UserServiceControl implements IUserServiceGate{

    private static final Logger logger = LoggerFactory.getLogger(UserServiceControl.class);


    @Autowired
    UserServiceProxy userService;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @RequestMapping(value = "/login")
    @ResponseBody
    public Result login(
            @RequestParam String username, @RequestParam String password, @RequestParam(required = false) String captcha){
//说明： 发起时       从终端 -- cookie1--> Feign -- cookie2-->Service 中，cookie2的复制通过interceptor完成
//      返回时       从Serivice--cookie3-->Feign--cookie4-->终端 的过程中，cookie4的session传播，如果不通过ResponseEntity ，则只能手工添加？存疑
        Result result=userService.login(username,password,captcha);
        logger.info("post login .......");
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
//        if(result.isSuccess())
//        {
//            request.removeAttribute(SessionRepositoryFilter.SESSION_REPOSITORY_ATTR+ ".CURRENT_SESSION");
//            String sessionCookieId=(String)result.getData();
//            DefaultCookieSerializer cookieSerializer=new DefaultCookieSerializer();
//            cookieSerializer.writeCookieValue(new CookieSerializer.CookieValue(request, response, sessionCookieId));
//
//        }

        return result ;
    }

    @RequestMapping(value = "/user")
    @ResponseBody
    public Result update(@RequestParam String mobile) {

        Result result= userService.update(mobile);
        return result;
    }

    @Override
    public Result<Integer> update(User user) {
        return userService.update(user);
    }


    @Override
    public Result<IUser> fetchHomepage() {
        return null;
    }

    @RequestMapping(value = "/logout")
    public Result logout()
    {
        return  userService.logout();
    }
    @RequestMapping(value = "/kid/register/{mac}")
    public Result register(@PathVariable("mac") String mac) {
        return userService.register(mac);
    }

    @Override
    public Result newBindToken() {
        return userService.newBindToken();
    }

    @Override
    public Result firebind(@PathVariable(value = "mac") String mac) {
        return userService.firebind(mac);
    }

    @Override
    public Result confirmBind(@PathVariable(value = "token") String token) {
        return userService.confirmBind(token);
    }

    @Override
    public Result queryBind(@PathVariable(value = "token") String token) {
        return userService.queryBind(token);
    }

    @Override
    public Result resetPassowrd( @RequestParam(value = "password") String password,
                                 @RequestParam(value = "mobileCaptcha") String captcha,
                                 @RequestParam(value = "mobile") String mobile,
                                 @CookieValue(value = SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME) String mobileCaptchaKey) {
        return userService.resetPassowrd(password,captcha,mobile,mobileCaptchaKey);
    }

    @Override
    public Result changePassowrd( @RequestParam(value = "password") String password,
                                  @RequestParam(value = "oldPassword") String oldPassword) {
        return userService.changePassowrd(password,oldPassword);
    }

    @RequestMapping(value = "/register")
    public Result register(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mobileCaptcha") String captcha,
            @RequestParam(value = "mobile") String mobile,
            @CookieValue(value= SecurityConstants.MOBILE_CAPTCHA_KEY_COOKIE_NAME) String mobileCaptchaKey
    ){
        return userService.register(username,password,captcha,mobile,mobileCaptchaKey);
    }
}
