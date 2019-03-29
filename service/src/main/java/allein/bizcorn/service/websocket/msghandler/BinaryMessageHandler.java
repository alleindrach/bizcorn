package allein.bizcorn.service.websocket.msghandler;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
@Service
public class BinaryMessageHandler extends BinaryWebSocketHandler {
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

    }

}
