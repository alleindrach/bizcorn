/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-01 10:10
 **/
public class WebsocketUtil {
    public static final Map<String, WebSocketSession> USERS_ONLINE = new ConcurrentHashMap();

    /**
     * 向所有在线用户发送消息(遍历 向每一个用户发送)
     * @param message
     */
    public static void sendMessageToAllOnlineUser(String message){
        USERS_ONLINE.forEach((username, Session) -> sendMessage(Session, message));
    }
    public static void addSession(String username,WebSocketSession session)
    {
        if(USERS_ONLINE.containsKey(username))
        {
            try {
                USERS_ONLINE.get(username).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        USERS_ONLINE.put(username,session);
    }
    public  static void removeSession(String username,Boolean close)
    {
        if(USERS_ONLINE.containsKey(username) && close)
        {
            try {
                USERS_ONLINE.get(username).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        USERS_ONLINE.remove(username);
    }
    /**
     * 向指定用户发送消息
     * @param session 用户session
     * @param message 发送消息内容
     */
    private static void sendMessage(WebSocketSession session, String message) {
        if (session == null) {
            return;
        }


        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static WebSocketSession getSessionOfUser(String username){
        return USERS_ONLINE.get(username);
    }
}
