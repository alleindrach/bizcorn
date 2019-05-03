/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.websocket;

import allein.bizcorn.model.mongo.Message;

/**
 * @program: bizcorn
 * @description: WS 消息处理器
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-02 19:19
 **/
public interface Handler {
    public void handle(Message mesage);
}
