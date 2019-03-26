package allein.bizcorn.service.controller;

import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.common.util.SecurityUtil;
import allein.bizcorn.facade.UserServiceFacade;
import allein.bizcorn.service.captcha.CaptchaImageHelper;
import allein.bizcorn.service.captcha.CaptchaMessageHelper;
import allein.bizcorn.service.captcha.CaptchaResult;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.security.SimpleAuthenticationManager;
import allein.bizcorn.service.security.config.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@EnableEurekaClient
@RestController
@RefreshScope
public class UserServiceController implements UserServiceFacade {


    private static final Logger logger = LoggerFactory.getLogger(UserServiceController.class);

    @Value("${bizcorn.session.prefix}")
    String sessionPrefix;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @Value("${bizcorn.session.attribute.timeout}")
    String sessionAttrTimeout;
    @Value("${bizcorn.session.timeout}")
    Long sessionTimeout;
    @Autowired
    private IUserService userService;

    @Autowired
    private SimpleAuthenticationManager simpleAuthenticationManager;

    @Autowired
    private CaptchaImageHelper captchaImageHelper;
    @Autowired
    private CaptchaMessageHelper captchaMessageHelper;


    public
    Result<User> logout(
            HttpServletResponse httpRsp,
            HttpSession session,
            HttpServletRequest request
    ) {
        SecurityContextHolder.getContext().setAuthentication(null);
        return new Result(1, "", null, null);
    }

    //        public
//    @RequestMapping("/login")
//    @ResponseBody
//    Result<User> login(
//            @RequestParam(value = "username") String username,
//            @RequestParam(value = "password") String password,
//            HttpServletResponse httpRsp,
//            HttpSession session,
//            HttpServletRequest request
//    ) {
//        Authentication authToken = new UsernamePasswordAuthenticationToken(name, password);
//        Authentication result = simpleAuthenticationManager.authenticate(authToken);
//        SecurityContextHolder.getContext().setAuthentication(result);
//
////
////        User user = userDAO.selectByNameCached(name);
////        if (user == null) {
////            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
////        } else {
//////            String sessionID = sessionPrefix + KeyGenerator.getKey();
////
////            // 将 SessionID-UserEntity 存入Redis
//////            redisService.set(sessionID, userEntity, sessionExpireTime);
//////        RedisServiceTemp.userMap.put(sessionID, userEntity);
//////
//////            // 将SessionID存入HTTP响应头
//////            Cookie cookie = new Cookie(sessionCookieName, sessionID);
//////            httpRsp.addCookie(cookie);
//////            Cookie cookieUser = new Cookie(sessionCookieUser, ((Integer) (user.getId())).toString());
//////            httpRsp.addCookie(cookieUser);
////            logger.info(" {} >>> {}",  request.getRequestURL().toString(),session.getId());
////            Enumeration e = request.getSession().getAttributeNames();
////            while (e.hasMoreElements()) {
////                String ename = (String) e.nextElement();
////                logger.info("Session >>>>> {} => {}",ename,request.getSession().getAttribute(ename));
////            }
////            Cookie[] cookies =request.getCookies();
////            if(cookies!=null) {
////                for (int i = 0; i < cookies.length; i++) {
////                    logger.info("Cookie >>>>> {} => {}", cookies[i].getName(), cookies[i].getValue());
////                }
////            }
////            session.setAttribute(sessionAttrTimeout,System.currentTimeMillis()/1000L+sessionTimeout);
////            session.setAttribute(sessionAttrUser, JSONObject.toJSONString(user));
//
////        }
//
//        return new Result(1, "", null, result);
//    }
    public
    @PreAuthorize("hasRole('USER')")
    @Transactional
//    @AuthLogin(injectUidFiled = "userId")
    Result<User> update(
            @RequestParam(value = "mobile") String mobile,
            Long userId,
            HttpSession session,
            HttpServletRequest request
    ) {
        logger.info(" {} >>> {}",  request.getRequestURL().toString(),session.getId());
        String username= SecurityUtil.getUserName();
        String test=(String)session.getAttribute("username");
        User user=userService.getUserByUsername(username);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        } else {
            user.setMobile(mobile);
            int updated=userService.updateUser(user);
            if(updated>0)
            {
                return new Result<User>(1, "", null, user);
            }else
            {
                return new Result<User>(0, "", null, user);
            }
        }
    }
//    @RequestMapping("/sendmsg")
//    public void send(String message){
//        LOG.info("sending message='{}' to topic='{}'", message, topic);
//        kafkaTemplate.send(topic, message);
//    }
//
//    @KafkaListener(topics = "${spring.kafka.template.default-topic}")
//    public void receive(@Payload String message,
//                        @Headers MessageHeaders headers) {
//        LOG.info("received message='{}'", message);
//        headers.keySet().forEach(key -> LOG.info("{}: {}", key, headers.get(key)));
//    }

    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        captchaImageHelper.generateAndWriteCaptchaImage(request,response, SecurityConstants.SECURITY_KEY);
    }

    public Result mobileCaptcha(@RequestParam String mobile) {
        User params = new User();
        params.setMobile(mobile);
        if (userService.getUserByMobile(mobile) == null) {
            return new Result();
        }
        else
        {
            CaptchaResult captchaResult = captchaMessageHelper.generateMobileCaptcha(mobile, SecurityConstants.SECURITY_KEY);
            if (captchaResult.isSuccess()) {
                // 模拟发送验证码
                logger.info("【BizCorn】 您的短信验证码是 {}。若非本人发送，请忽略此短信。", captchaResult.getCaptcha());
                captchaResult.clearCaptcha();
                return Result.successWithData(captchaResult);
            }

            return Result.failWithMessage(captchaResult.getMessage());
        }

    }
}
