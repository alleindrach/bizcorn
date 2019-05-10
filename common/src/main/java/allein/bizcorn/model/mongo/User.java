package allein.bizcorn.model.mongo;

import allein.bizcorn.common.util.Masker;
import allein.bizcorn.common.websocket.Status;
import allein.bizcorn.model.facade.IProfile;
import allein.bizcorn.model.facade.IUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Document(collection="Users")
public class User  implements  IUser {

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
    public JSONObject toResultJson() {
        JSONObject result= (JSONObject) JSON.toJSON(this);
        if(result.getString("mobile")!=null)
            result.put("mobile",Masker.getMaskCharWay(result.getString("mobile"),3,9));
        if(curPartner!=null)
        {
            result.put("curPartner",this.getCurPartner().getUsername());
        }
        return result;
    }
}
