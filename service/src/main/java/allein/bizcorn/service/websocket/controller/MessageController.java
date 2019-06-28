package allein.bizcorn.service.websocket.controller;

import allein.bizcorn.model.mongo.Kid;
import allein.bizcorn.model.mongo.Message;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IMessageBrokerService;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.implement.UserServiceMongoImpl;
import allein.bizcorn.service.task.ScheduledTask;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private IUserService userService;
    @Autowired
    private SimpMessagingTemplate msgTemplate;
    @Autowired
    private IMessageBrokerService messageBrokerService;
// @MessageMapping 标识客户端发来消息的请求地址，前面我们全局配置中制定了服务端接收的地址以“/center”开头，
// 所以客户端发送消息的请求连接是：/center/message；
    @MessageMapping("/message")
//    @SendToUser(value = "/topic/message",broadcast = false) //可以将消息只返回给发送者
    //@SendTo("/topic/message") //会将消息广播给所有订阅/message这个路径的用户
    public JSONObject selftalk(JSONObject message,
                               @Header(value="simpUser") UsernamePasswordAuthenticationToken user,
                               @Header(value="simpSessionId") String sessionId) throws Exception {
        logger.info("Received from user:{}@{}",((User)user.getPrincipal()).getUsername(),sessionId);

        return JSONObject.parseObject("{responseto:"+message.toJSONString()+"}");

    }
    @MessageMapping("/brodcast/message")
    @SendToUser(value = "/topic/message",broadcast = true) //可以将消息广播给该用户的所有终端
    //@SendTo("/topic/message") //会将消息广播给所有订阅/message这个路径的用户
    public JSONObject selftalkb(JSONObject message) throws Exception {
        return JSONObject.parseObject("{responseto:"+message.toJSONString()+"}");

    }
    //定向发送
    @RequestMapping(value = "/msg/send/{username}/{message}")
    public void sendTo(@PathVariable  String username,@PathVariable  String message)
    {
        msgTemplate.convertAndSendToUser(username, "/topic/message",message);

    }
    //群聊
    @MessageMapping("/group/{groupId}")
    public void groupMessage(String message, @DestinationVariable String groupId){
        String dest = "/group/" + groupId + "/message" ;
        msgTemplate.convertAndSend(dest, message);
    }
    /*
    @Description:发给绑定用户的消息
    @Param:
    @Return:
    @Author:Alleindrach@gmail.com
    @Date:2019/6/28
    @Time:9:51 AM
    */
    @MessageMapping("/partner/")
    public void talk(JSONObject message,
                               @Header(value="simpUser") UsernamePasswordAuthenticationToken user,
                               @Header(value="simpSessionId") String sessionId) throws Exception {
        String username=((User)user.getPrincipal()).getUsername();
        logger.info("Received from user:{}@{}",username,sessionId);

        allein.bizcorn.model.mongo.User userInDB=userService.getUser(username);
        if(userInDB==null)
        {
            logger.info("User:{}  error!",username);
            return;
        }
        if(userInDB.getCurPartner()==null)
        {
            logger.info("User:{} partner error!",username);
            return;
        }
        //重新绑定
        try {
            if (userInDB.getCurPartner() instanceof Kid) {
                userService.rebind(
                        userInDB,
                        (Kid) userInDB.getCurPartner());
            }
        }
        catch(Exception ex)
        {
            logger.info("rebindError:",ex);
        }
        msgTemplate.convertAndSendToUser(userInDB.getCurPartner().getUsername(),"/topic/message",message);
        Message ackMsg = Message.AckMessage(message,userInDB);
        messageBrokerService.send(ackMsg);

        return ;

    }
}
