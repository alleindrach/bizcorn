package allein.service.service;

import allein.model.user.Result;
import allein.model.user.User;
import allein.service.ServiceApplication;
import allein.service.config.Configuration;
import allein.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableEurekaClient
@RestController
@RefreshScope
public class UserManageService {


    private static final Logger LOG = LoggerFactory.getLogger(UserManageService.class);

    @Autowired
    Configuration config;


    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/login")
    public Result<User> login(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "password") String password,
            HttpServletResponse httpRsp,
            HttpSession session
    ) {

        User user= userMapper.selectByName(name);

        return new Result<User>(1,"",user);
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
