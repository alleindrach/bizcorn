package allein.bizcorn.service.facade;

import org.springframework.beans.factory.annotation.Autowired;

public interface IMessageService {

    void send(String topic ,String message);

}
