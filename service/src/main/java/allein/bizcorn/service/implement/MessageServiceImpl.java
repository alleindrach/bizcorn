package allein.bizcorn.service.implement;

import allein.bizcorn.service.facade.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class MessageServiceImpl implements IMessageService {
    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void send(String topic, String message) {
        kafkaTemplate.send(topic,message);
    }
}
