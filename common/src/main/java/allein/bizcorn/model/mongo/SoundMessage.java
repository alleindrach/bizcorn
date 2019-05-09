/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.model.facade.IMessage;
import allein.bizcorn.model.output.IResultor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection="SoundMessage")
//@CompoundIndexes({
//        @CompoundIndex(name = "session_index", def = "{'sessionId', 1, 'createDate': -1}")
//
//})
public class SoundMessage implements IResultor {
    @Id
    @Getter @Setter
    private String id;

    @JSONField(serialize = false)
    @DBRef
    @Getter @Setter
    private User talker;

    @JSONField(serialize = false)
    @DBRef
    @Setter @Getter
    private User talkee;

    @Setter @Getter
    private String snd;

    @Setter @Getter
    private Integer channel;

    @Setter @Getter
    private Date createDate;
    @Setter @Getter
    private MessageStatus status;//0=已收到， 1=已送达， 2=已阅读
    @Setter @Getter
    private Date deliverDate;//送达时间
    @Setter @Getter
    private Date copyDate;//阅读时间


    @Override
    public JSONObject toResultJson() {
        return (JSONObject) JSON.toJSON(this);
    }
}
