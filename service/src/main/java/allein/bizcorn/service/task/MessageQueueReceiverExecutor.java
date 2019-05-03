/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.task;

import allein.bizcorn.common.mq.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-02 18:29
 **/

@Component
public class MessageQueueReceiverExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MessageQueueReceiverExecutor.class);

    @Async
    public Future<String> doDispatch(Receiver receiver,String topic,
                                     String key,
                                     String ts,
                                     String message) throws InterruptedException{
        logger.info("MQ Topic:{} Received.",topic);
        long start = System.currentTimeMillis();
        receiver.onMessage(topic,key,ts,message);
        long end = System.currentTimeMillis();

        logger.info("MQ Topic:{}, time elapsed: {} ms.",topic, end-start);

        return new AsyncResult<>("accomplished!");
    }

}
