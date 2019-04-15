package allein.bizcorn.service.websocket.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate msgTemplate;
// @MessageMapping 标识客户端发来消息的请求地址，前面我们全局配置中制定了服务端接收的地址以“/center”开头，
// 所以客户端发送消息的请求连接是：/center/message；
    @MessageMapping("/message")
    @SendToUser(value = "/topic/message",broadcast = false) //可以将消息只返回给发送者
    //@SendTo("/topic/message") //会将消息广播给所有订阅/message这个路径的用户
    public JSONObject selftalk(JSONObject message) throws Exception {
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
}
