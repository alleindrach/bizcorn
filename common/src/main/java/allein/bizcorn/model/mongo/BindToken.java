/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-09 09:08
 **/
@Document(collection="BindToken")
public class BindToken  implements Serializable {

    @Id
    @Getter
    @Setter
    private String id;
    @DBRef(lazy = true)
    @Indexed
    @Setter
    @JSONField(serializeUsing = UserSerializer.class)
    private User binder;
    @DBRef(lazy = true)
    @Indexed
    @Setter
    @JSONField(serializeUsing = UserSerializer.class)
    private User bindee;
    @Getter    @Setter
    private Date createDate;
    @Getter    @Setter
    private Date bindDate;
    @Getter    @Setter
    private BindTokenStatus status=BindTokenStatus.INIT;

    public BindToken(){

    }

    public BindToken(User bindee)
    {
        this.bindee=bindee;
        this.createDate=new Date();
        this.status=BindTokenStatus.INIT;

    }

    public User getBinder() {
        if(binder instanceof LazyLoadingProxy)
            return (User) ((LazyLoadingProxy)binder).getTarget();
        return binder;
    }

    public User getBindee() {
        if(bindee instanceof LazyLoadingProxy)
            return (User) ((LazyLoadingProxy)bindee).getTarget();
        return bindee;
    }
}
