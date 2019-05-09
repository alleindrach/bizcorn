/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.facade;

import allein.bizcorn.common.mq.Receiver;
import allein.bizcorn.common.mq.Topic;
import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.common.websocket.Handler;
import allein.bizcorn.model.mongo.Message;
import com.alibaba.fastjson.JSONObject;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.userdetails.User;

public interface IMessageBrokerService {


    void send(Message message);

    void dispatch(JSONObject message,String sender,String sessionId);

    void registerHandler(Action action, Handler handler);
}
