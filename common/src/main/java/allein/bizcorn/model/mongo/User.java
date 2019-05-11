package allein.bizcorn.model.mongo;

import allein.bizcorn.common.util.Masker;
import allein.bizcorn.common.websocket.Status;
import allein.bizcorn.model.facade.IProfile;
import allein.bizcorn.model.facade.IUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Document(collection="Users")
public class User  implements  IUser,JSONSerializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Setter
    private String id;

    @Indexed(unique = true)
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    @JSONField(serialize = false)
    private String password;
    @Indexed
    @Getter
    @Setter
    private String mobile;
    @Getter
    @Setter
    private Integer enabled;
    @Getter
    @Setter
    private IProfile profile;
    @Getter
    @Setter
    @JSONField(serialzeFeatures = { SerializerFeature.WriteNullListAsEmpty})
    Set<String> Authorities;
    @Getter
    @Setter
    private Date createDate;
    @Getter
    @Setter
    private Date lastVisit;
    @Getter
    @Setter
    private Status status;
    @JSONField(serialize = false)
    @DBRef
    @Getter
    @Setter
    private User curPartner;
    @Getter
    @Setter
    protected  Role role=Role.ADULT;


    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
        JSONObject jsonUser=new JSONObject();
        jsonUser.put("id",this.getId());
        jsonUser.put("username",this.getUsername());
        serializer.write(jsonUser);
    }
}
