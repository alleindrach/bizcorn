/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import allein.bizcorn.model.output.IResultor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-09 09:08
 **/
@Document(collection="BindToken")
public class BindToken  implements IResultor {

    @Id
    @Getter
    @Setter
    private String id;
    @DBRef
    @Indexed
    @Getter    @Setter
    @JSONField(serialize = false)
    private User binder;
    @DBRef
    @Indexed
    @Getter    @Setter
    @JSONField(serialize = false)
    private User bindee;
    @Getter    @Setter
    private Date createDate;
    @Getter    @Setter
    private Date bindDate;
    @Getter    @Setter
    private BindTokenStatus status=BindTokenStatus.INIT;

    public BindToken(){

    }
    public BindToken(User binder,User bindee)
    {
        this.bindee=bindee;
        this.binder=binder;
        this.createDate=new Date();
        this.status=BindTokenStatus.INIT;

    }

    @Override
    public JSONObject toResultJson() {
        JSONObject result= (JSONObject) JSON.toJSON(this);
        result.put("binder",binder.getUsername());
        result.put("bindee",binder.getUsername());
        return result;
    }
}
