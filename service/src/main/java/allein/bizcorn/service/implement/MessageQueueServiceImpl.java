package allein.bizcorn.service.implement;
import allein.bizcorn.common.mq.Receiver;
import allein.bizcorn.common.mq.Topic;
import allein.bizcorn.service.facade.IMessageQueueService;
import allein.bizcorn.service.facade.IUserService;
import allein.bizcorn.service.task.MessageQueueReceiverExecutor;
import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Component
public class MessageQueueServiceImpl implements IMessageQueueService,InitializingBean {
    private HashMap<String,List<Receiver>> receivers=new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MessageQueueServiceImpl.class);
    @Value("${spring.kafka.template.default-topic}")
    String defaultTopic;
    @Value("${spring.kafka.template.topic-pattern-prefix}")
    String topicPrefix;
    @Value("${bizcorn.thumbnail.small.width}")
    Integer small_width;
    @Value("${bizcorn.thumbnail.small.height}")
    Integer small_height;
    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;
    @Autowired
    private MessageQueueReceiverExecutor messageQueueReceiverExecutor;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations operations;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private IUserService userService;
    @Override
    public void send(String message) {
        kafkaTemplate.send(defaultTopic,message);
    }

    @Override
    public void send(Object message) {
        kafkaTemplate.send(defaultTopic, JSON.toJSONString(message));
    }

    @Override
    public void send(Topic topic, String message) {
        kafkaTemplate.send(topicPrefix+topic.getValue(),message);
    }

    @Override
    public void send(Topic topic, Object message) {
        kafkaTemplate.send(topicPrefix+topic.getValue(), JSON.toJSONString(message));
    }

    @KafkaListener(topicPattern ="${spring.kafka.template.topic-pattern}")
    public void topicPatternReceive(
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.RECEIVED_MESSAGE_KEY,required = false) String key,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String ts,
            @Payload String message/*,
            @Headers MessageHeaders headers*/) {
        logger.info("received message='{}',@Topic:{}", message,topic);
        String subTopic=topic.replaceFirst(topicPrefix,"");
        if(this.receivers!=null){
            List<Receiver> receiversOfTopic=receivers.get(subTopic);
            if(receiversOfTopic!=null)
            {
                receiversOfTopic.forEach(receiver->{
                    try {
                        messageQueueReceiverExecutor.doDispatch(receiver,topic,key,ts,message);
                    } catch (InterruptedException e) {
                        logger.error("消息处理错误:topic:{}",topic,e);
                    }
//                    receiver.onMessage(topic,key,ts,message);
                });
            }
        }
    }

    @Override
    public void registerReceiver(Topic topic, Receiver recciver) {
        List receiversOfTopic=receivers.get(topic.getValue());
        if(receiversOfTopic==null){
            receiversOfTopic=new ArrayList(10);
            receivers.put(topic.getValue(),receiversOfTopic);
        }
        receiversOfTopic.add(recciver);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registerReceiver(Topic.IMAGE_THUMB, new Receiver() {
            @Override
            public void onMessage(String topic, String key, String ts, String message) {
                String fileId=message;
                Query query = Query.query(Criteria.where("_id").is(fileId));
                GridFSFile gfsfile = gridFsTemplate.findOne(query);
                String md5Name=gfsfile.getFilename();
                String thumbName=md5Name+".small";
                GridFSFile thumbGFsFile=gridFsTemplate.findOne(Query.query(GridFsCriteria.whereFilename() .is(thumbName)));
                if(thumbGFsFile==null) {
                    GridFSDownloadStream in = gridFSBucket.openDownloadStream(gfsfile.getObjectId());
                    try {
                        BufferedImage originalImage = ImageIO.read(in);
                        BufferedImage thumbnail = Thumbnails.of(originalImage)
                                .size(small_width, small_height)
                                .asBufferedImage();
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
                        ImageIO.write(thumbnail, gfsfile.getMetadata().getString("_fileSuffix"), imOut);
                        InputStream is = new ByteArrayInputStream(bs.toByteArray());
                        ObjectId gridFSFileId = gridFsTemplate.store(is, md5Name + ".small", gfsfile.getMetadata());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        this.registerReceiver(Topic.USER_WS_CONNECTED, new Receiver() {
            @Override
            public void onMessage(String topic, String key, String ts, String message) {
                logger.info("on user login message:{}",message);
                String username=message;
                userService.checkIn(username);

            }
        });
        this.registerReceiver(Topic.USER_WS_DISCONNECTED, new Receiver() {
            @Override
            public void onMessage(String topic, String key, String ts, String message) {
                logger.info("on user logout message:{}",message);
                String username=message;
                userService.checkOut(username);

            }
        });
    }
}
