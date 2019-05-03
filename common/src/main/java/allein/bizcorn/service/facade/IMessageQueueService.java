package allein.bizcorn.service.facade;

import allein.bizcorn.common.mq.Receiver;
import allein.bizcorn.common.mq.Topic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

public interface IMessageQueueService{

    void send( String message);
    void send( Object message);
    void send(Topic topic,String message);
    void send(Topic topic,Object message);

    void topicPatternReceive(  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                      @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String ts,
                                      @Payload String message);

    void registerReceiver(Topic topic, Receiver recciver);
}
