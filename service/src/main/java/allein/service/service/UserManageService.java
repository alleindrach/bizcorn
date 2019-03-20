package allein.service.service;

import allein.model.data.user.User;
import allein.model.exception.CommonException;
import allein.model.exception.ExceptionEnum;
import allein.model.output.Result;
import allein.service.dao.UserDAO;
import allein.util.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableEurekaClient
@RestController
@RefreshScope
@RequestMapping("/user")
public class UserManageService {


    private static final Logger LOG = LoggerFactory.getLogger(UserManageService.class);

    @Value("${bizcorn.session.prefix}")
    String sessionPrefix;
    @Value("${bizcorn.session.cookie.name}")
    String sessionCookieName;
    @Value("${bizcorn.session.cookie.user}")
    String sessionCookieUser;
    @Autowired
    private UserDAO userDAO;

    public
    @RequestMapping("/login")
    @ResponseBody
    Result<User> login(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password,
            HttpServletResponse httpRsp,
            HttpSession session
    ) {

        User user = userDAO.selectByName(name);
        if (user == null) {
            throw new CommonException(ExceptionEnum.USER_ACCOUNT_NOT_EXIST);
        } else {
            String sessionID = sessionPrefix + KeyGenerator.getKey();

            // 将 SessionID-UserEntity 存入Redis
//            redisService.set(sessionID, userEntity, sessionExpireTime);
//        RedisServiceTemp.userMap.put(sessionID, userEntity);

            // 将SessionID存入HTTP响应头
            Cookie cookie = new Cookie(sessionCookieName, sessionID);
            httpRsp.addCookie(cookie);
            Cookie cookieUser = new Cookie(sessionCookieUser, ((Integer) (user.getId())).toString());
            httpRsp.addCookie(cookieUser);
        }

        return new Result<User>(1, "", null, user);
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
