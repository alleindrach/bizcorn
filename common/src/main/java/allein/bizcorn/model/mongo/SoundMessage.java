/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.model.facade.IMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

@Document(collection="SoundMessage")
//@CompoundIndexes({
//        @CompoundIndex(name = "session_index", def = "{'sessionId', 1, 'createDate': -1}")
//
//})
public class SoundMessage implements  Serializable //,JSONSerializable
{
    @Id
    @Getter @Setter
    private String id;

    @DBRef(lazy = true)
    @Setter
    @JSONField(serializeUsing =  UserSerializer.class)
    private User talker;

    @DBRef(lazy = true)
    @Setter
    @JSONField(serializeUsing =  UserSerializer.class)
    private User talkee;

    @Setter @Getter
    private String snd;

    @Setter @Getter
    private Integer channel;

    @Getter
    @Setter
    private List<String> tags;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String desc;

    @Getter
    @Setter
    private AuditStatus auditStatus=AuditStatus.NONE;


    @Setter @Getter
    @Indexed(direction = IndexDirection.DESCENDING)
    @JSONField(serialzeFeatures = {SerializerFeature.WriteDateUseDateFormat})
    private Date createDate;
    @Setter @Getter
    private MessageStatus status;//0=已收到， 1=已送达， 2=已阅读
    @Setter @Getter
    @JSONField(serialzeFeatures = {SerializerFeature.WriteDateUseDateFormat})
    private Date deliverDate;//送达时间
    @Setter @Getter
    private Date copyDate;//阅读时间
    @Setter @Getter
    private Date auditFireDate;//送审时间
    @Setter @Getter
    private Date auditDate;//审核时间

    public User getTalker() {
        if(this.talker instanceof LazyLoadingProxy)
            return (User) ((LazyLoadingProxy)talker).getTarget();
        return talker;
    }


    public User getTalkee() {
        if(this.talkee instanceof LazyLoadingProxy)
            return (User) ((LazyLoadingProxy)talkee).getTarget();
        return talkee;
    }


    public Boolean isValidOwner(User user)
    {
        if(getTalker().getId().compareToIgnoreCase(user.getId())==0)
        {
            return true;
        }
        if(getTalkee().getId().compareToIgnoreCase(user.getId())==0)
        {
            return true;
        }
        if(getTalkee() instanceof  Kid
                && (Kid)((Kid) getTalkee()).getParent() !=null
                && ((Kid)((Kid) getTalkee())).getParent().getId().compareToIgnoreCase(user.getId())==0)
        {
            return true;
        }
        return false;
    }
    public boolean isPublished()
    {
        return auditStatus.equals(AuditStatus.APPROVED);
    }

}
