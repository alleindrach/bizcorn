/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.mq;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.MessageHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-04-30 16:01
 **/
public class DefaultReciever implements Receiver {
    private static final Logger logger = LoggerFactory.getLogger(DefaultReciever.class);

    @Override
    public void onMessage(
            String topic,
            String key,
            String ts,
            String message){
        logger.info("received message='{}',@Topic:{}", message,topic);
    }
}
