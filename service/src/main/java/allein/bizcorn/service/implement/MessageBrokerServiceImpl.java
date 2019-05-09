/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.implement;
import allein.bizcorn.common.exception.ExceptionEnum;
import allein.bizcorn.common.mq.Receiver;
import allein.bizcorn.common.mq.Topic;
import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.common.websocket.Handler;
import allein.bizcorn.model.mongo.Message;
import allein.bizcorn.model.mongo.MessageStatus;
import allein.bizcorn.service.db.mongo.dao.UserDAO;
import allein.bizcorn.service.facade.IMessageBrokerService;
import allein.bizcorn.service.facade.IMessageQueueService;
import allein.bizcorn.service.task.MessageQueueReceiverExecutor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import net.coobird.thumbnailator.Thumbnails;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class MessageBrokerServiceImpl implements IMessageBrokerService,InitializingBean {
    private HashMap<String,Handler> handlers=new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MessageBrokerServiceImpl.class);
    @Value("${spring.kafka.template.default-topic}")
    String defaultTopic;
    @Value("${spring.kafka.template.topic-pattern-prefix}")
    String topicPrefix;
    @Value("${bizcorn.thumbnail.small.width}")
    Integer small_width;
    @Value("${bizcorn.thumbnail.small.height}")
    Integer small_height;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private MessageQueueReceiverExecutor messageQueueReceiverExecutor;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations operations;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private UserDAO userDAO;
    @Override
    public void send(Message message)
    {
        String username=message.getDestName();
        simpMessagingTemplate.convertAndSendToUser(username, "/topic/message",message.toString());
    }

    public void dispatch(JSONObject messageJson, String sender, String sessionId) {
        logger.info("received message='{}',@Sender:{}", messageJson,sender);

        Message message=new Message();
        message.setAction(Action.valueOf(messageJson.getString("action")));
        message.setContent(messageJson.getString("content"));
        message.setCreateDate(new Date());
        message.setSrcName(sender);
        message.setStatus(MessageStatus.DISPATCHED);

        if(this.handlers!=null){
            Handler handler=handlers.get(message.getAction());
            if(handler!=null)
            {
                try{
                    handler.handle(message);
                } catch (Exception e) {
                    logger.error("消息处理错误:Message:{}",message,e);
                }
            }
        }
    }

    @Override
    public void registerHandler(Action action, Handler handler) {
        handlers.put(action.getValue(),handler);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
