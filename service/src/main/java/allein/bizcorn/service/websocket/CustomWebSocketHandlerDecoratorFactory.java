package allein.bizcorn.service.websocket;

import allein.bizcorn.service.implement.CacheServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.session.MapSession;
import org.springframework.session.web.socket.events.SessionConnectEvent;
import org.springframework.session.web.socket.handler.WebSocketConnectHandlerDecoratorFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

import java.security.Principal;
import java.util.Arrays;
@Component
public class CustomWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {
    private static final Logger logger = LoggerFactory.getLogger(CustomWebSocketHandlerDecoratorFactory.class);

    @Override
    public WebSocketHandler decorate(WebSocketHandler webSocketHandler) {
        return new SessionWebSocketHandler(webSocketHandler);
    }
    private final class SessionWebSocketHandler extends ExceptionWebSocketHandlerDecorator {

        SessionWebSocketHandler(WebSocketHandler delegate) {
            super(delegate);
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession wsSession) {

            Principal user = wsSession.getPrincipal();

            logger.debug(  "wsSession {} connected with  user {} ",wsSession.getId(),user.getName());
            
            super.afterConnectionEstablished(wsSession);

        }

        @Override
        public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus closeStatus) {


            Principal user = wsSession.getPrincipal();

            logger.debug(  "wsSession {} closed with {} ,user {} ",wsSession.getId(),  closeStatus,user.getName());


            super.afterConnectionClosed(wsSession, closeStatus);
        }

    }
}
