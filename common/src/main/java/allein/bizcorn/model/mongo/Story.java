/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IStory;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-06-25 17:17
 **/
@Document(collection="Story")
public abstract class  Story implements IStory,Serializable {

    @Id
    @Getter
    @Setter
    protected String id;

    @DBRef(lazy = true)
    @Setter
    @JSONField(serializeUsing =  UserSerializer.class)
    protected User talker;

    @DBRef(lazy = true)
    @Setter
    @JSONField(serializeUsing =  UserSerializer.class)
    protected User talkee;


    @Setter @Getter
    protected Integer channel;

    @Getter
    @Setter
    protected List<String> tags;

    @Getter
    @Setter
    protected String name;

    @Getter
    @Setter
    protected StoryType type=StoryType.SOUND;


    @Getter
    @Setter
    protected String desc;


    @Getter
    @Setter
    @DBRef(lazy = true)
    protected List<Comment> comments;


    @Getter
    @Setter
    protected Integer likes;

    @Getter
    @Setter
    protected Integer downloads;
    @Getter
    @Setter
    protected AuditStatus auditStatus=AuditStatus.NONE;


    @Setter @Getter
    @Indexed(direction = IndexDirection.DESCENDING)
    @JSONField(serialzeFeatures = {SerializerFeature.WriteDateUseDateFormat})
    protected Date createDate;
    @Setter @Getter
    protected MessageStatus status=MessageStatus.INIT;//0=已收到， 1=已送达， 2=已阅读
    @Setter @Getter
    @JSONField(serialzeFeatures = {SerializerFeature.WriteDateUseDateFormat})
    protected Date deliverDate;//送达时间
    @Setter @Getter
    protected Date copyDate;//阅读时间
    @Setter @Getter
    protected Date auditFireDate;//送审时间
    @Setter @Getter
    protected Date auditDate;//审核时间

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

    public abstract Object getData();
    public abstract void setData(Object v);
}
