package allein.bizcorn.service.controller;

import allein.bizcorn.common.aspect.AuthLogin;
import allein.bizcorn.common.exception.CommonException;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.model.entity.user.User;
import allein.bizcorn.common.model.output.Result;
import allein.bizcorn.service.dao.UserDAO;
import allein.bizcorn.service.security.SimpleAuthenticationManager;
import allein.bizcorn.service.security.CustomUserDetailsService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;

@EnableEurekaClient
@RestController
@RefreshScope
@RequestMapping("/user")
public class UserManageService {


    private static final Logger logger = LoggerFactory.getLogger(UserManageService.class);

    @Value("${bizcorn.session.prefix}")
    String sessionPrefix;

    @Value("${bizcorn.session.attribute.user}")
    String sessionAttrUser;

    @Value("${bizcorn.session.attribute.timeout}")
    String sessionAttrTimeout;
    @Value("${bizcorn.session.timeout}")
    Long sessionTimeout;
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SimpleAuthenticationManager simpleAuthenticationManager;

    public
    @RequestMapping("/logout")
    @ResponseBody
    Result<User> logout(
            HttpServletResponse httpRsp,
            HttpSession session,
            HttpServletRequest request
    ) {
        SecurityContextHolder.getContext().setAuthentication(null);
        return new Result(1, "", null, null);
    }
        public
    @RequestMapping("/login")
    @ResponseBody
    Result<User> login(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password,
            HttpServletResponse httpRsp,
            HttpSession session,
            HttpServletRequest request
    ) {


        Authentication authToken = new UsernamePasswordAuthenticationToken(name, password);



        Authentication result = simpleAuthenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(result);


//
//        User user = userDAO.selectByNameCached(name);
//        if (user == null) {
//            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
//        } else {
////            String sessionID = sessionPrefix + KeyGenerator.getKey();
//
//            // 将 SessionID-UserEntity 存入Redis
////            redisService.set(sessionID, userEntity, sessionExpireTime);
////        RedisServiceTemp.userMap.put(sessionID, userEntity);
////
////            // 将SessionID存入HTTP响应头
////            Cookie cookie = new Cookie(sessionCookieName, sessionID);
////            httpRsp.addCookie(cookie);
////            Cookie cookieUser = new Cookie(sessionCookieUser, ((Integer) (user.getId())).toString());
////            httpRsp.addCookie(cookieUser);
//            logger.info(" {} >>> {}",  request.getRequestURL().toString(),session.getId());
//            Enumeration e = request.getSession().getAttributeNames();
//            while (e.hasMoreElements()) {
//                String ename = (String) e.nextElement();
//                logger.info("Session >>>>> {} => {}",ename,request.getSession().getAttribute(ename));
//            }
//            Cookie[] cookies =request.getCookies();
//            if(cookies!=null) {
//                for (int i = 0; i < cookies.length; i++) {
//                    logger.info("Cookie >>>>> {} => {}", cookies[i].getName(), cookies[i].getValue());
//                }
//            }
//            session.setAttribute(sessionAttrTimeout,System.currentTimeMillis()/1000L+sessionTimeout);
//            session.setAttribute(sessionAttrUser, JSONObject.toJSONString(user));

//        }

        return new Result(1, "", null, result);
    }
    public
    @PutMapping("/")
    @ResponseBody
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

        String userPrincipal=SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//
//        Enumeration e = request.getSession().getAttributeNames();
//        while (e.hasMoreElements()) {
//            String ename = (String) e.nextElement();
//            logger.info("Session >>>>> {} => {}",ename,request.getSession().getAttribute(ename));
//        }
//        Cookie[] cookies =request.getCookies();
//        if(cookies!=null) {
//            for (int i = 0; i < cookies.length; i++) {
//                logger.info("Cookie >>>>> {} => {}", cookies[i].getName(), cookies[i].getValue());
//            }
//        }
//        Object uo=session.getAttribute(sessionAttrUser);
//
//        User user = (User) JSONObject.parseObject(uo.toString(),User.class);
        User user=userDAO.selectByName(userPrincipal);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        } else {
            user.setMobile(mobile);
            int updated=userDAO.update(user);
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
}
