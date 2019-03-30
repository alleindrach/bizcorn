package allein.bizcorn.service.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {
    @Autowired
    private DefaultHandshakeHandler defaultHandshakeHandler;
    public WebSocketHandshakeHandler() {
    }

    public WebSocketHandshakeHandler(RequestUpgradeStrategy requestUpgradeStrategy) {
        super(requestUpgradeStrategy);
    }
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Principal user= super.determineUser(request,wsHandler,attributes);
//        principal 是 UsernamePasswordAuthenticationToken,其principal字段是CustomUserDetailsService.loadUserByUsername 获取到的UserDetails
        if(user==null)
            throw new HandshakeFailureException("未取得用户凭据");
        return user;
    }
//    @Override
//    public boolean doHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws HandshakeFailureException {
//        return defaultHandshakeHandler.doHandshake(serverHttpRequest,serverHttpResponse,webSocketHandler,map);
//    }
}
