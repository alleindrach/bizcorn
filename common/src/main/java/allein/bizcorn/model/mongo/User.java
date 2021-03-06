package allein.bizcorn.model.mongo;

import allein.bizcorn.common.util.Masker;
import allein.bizcorn.common.websocket.Status;
import allein.bizcorn.model.facade.IProfile;
import allein.bizcorn.model.facade.IUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.*;
import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Document(collection="Users")
public class User   implements IUser, JSONSerializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Getter
    @Setter
    @JsonProperty
    private String id;

    @Indexed(unique = true)
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    @JsonIgnore
    @JSONField(serialize = false)
    private String password;
    @Indexed
    @Getter
    @Setter
    private String mobile;
    @Getter
    @Setter
    private Integer enabled=1;
    @Getter
    @Setter
    private IProfile profile;
    @Getter
    @Setter
    @JSONField(serialzeFeatures = { SerializerFeature.WriteNullListAsEmpty})
    List<String> authorities;
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
    @DBRef(lazy = true)
    @Setter
    private User curPartner;
    @Getter
    @Setter
    protected  Role role=Role.ADULT;
    @DBRef(lazy = true)
    @Setter
    private List<User> friends;

    public User getCurPartner() {
        if(this.curPartner instanceof LazyLoadingProxy)
            return (User) ((LazyLoadingProxy)curPartner).getTarget();
        return curPartner;
    }


    static public class SimpleSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            JSONObject jsonUser=new JSONObject();
            User user=(User) object;
            jsonUser.put("id",user.getId());
            jsonUser.put("username",user.getUsername());
            jsonUser.put("profile",user.getProfile());
            serializer.write(jsonUser);
        }
    }
    static public class FullSerializer implements ObjectSerializer {

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            JSONObject jsonUser=new JSONObject();
            SerializeConfig config=new SerializeConfig();
            config.put(User.class,new User.SimpleSerializer());
            config.put(Kid.class,new User.SimpleSerializer());

            User user=(User) object;
            jsonUser.put("id",user.getId());
            jsonUser.put("username",user.getUsername());
            if(user.getFriends()!=null && user.getFriends().size()>0)
                jsonUser.put("friends",JSON.toJSONString(user.getFriends(),config));
            jsonUser.put("profile",user.getProfile());
            if(user.getCurPartner()!=null)
                jsonUser.put("curPartner",JSON.toJSONString(user.getCurPartner(),config));
            if(user.getAuthorities()!=null && user.getAuthorities().size()>0)
                jsonUser.put("authorities",user.getAuthorities());
            jsonUser.put("enabled",user.getEnabled());
            jsonUser.put("mobile",user.getMobile());
            jsonUser.put("createDate",user.getCreateDate());
            jsonUser.put("lastVisit",user.getLastVisit());
            jsonUser.put("role",user.getRole());
            serializer.write(jsonUser);
        }
    }
    @Override
    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
        JSONObject jsonUser=new JSONObject();
        jsonUser.put("id",this.getId());
        jsonUser.put("username",this.getUsername());
        jsonUser.put("profile",getProfile());
        serializer.write(jsonUser);
    }
    public void addFriend(User user)
    {
        if(this.friends==null)
            this.friends= new ArrayList<>(10);

        for (Object o :this.friends
             ) {
            User friend=null;
            if(o instanceof LazyLoadingProxy)
            {
                friend= (User) ((LazyLoadingProxy) o).getTarget();
            }
            else
                friend=(User )o;
            if(friend.getId().compareToIgnoreCase(user.getId())==0)
            {
                return;
            }
        }
        if(friends.size()>10)
            friends.remove(0);
        this.friends.add(user);
    }
    public List<User> getFriends(){
        List<User> realFriends=new ArrayList<>(10);
        if(friends!=null && friends.size()>0){
            for(Object o:friends){
                if(o instanceof  LazyLoadingProxy)
                    realFriends.add((User) ((LazyLoadingProxy) o).getTarget());
                else
                    realFriends.add((User)o);
            }
        }
        return realFriends;
    }
    public Boolean hasFriend(String id){
        for (Object o:
             friends) {
            if(o instanceof LazyLoadingProxy)
            {
                if(((User)((LazyLoadingProxy) o).getTarget()).getId().compareToIgnoreCase(id)==0)
                    return true;
            }else
            {
                if(((User)o).getId().compareToIgnoreCase(id)==0)
                    return true;
            }

        }
        return false;
    }

    public  String fullJsonString(){
        SerializeConfig config=new SerializeConfig();
        config.put(User.class,new User.FullSerializer());
        config.put(Kid.class,new User.FullSerializer());
        return JSON.toJSONString(this,config);
    }
    public Kid getKid(){
        if(this instanceof  Kid)
            return (Kid) this;
        else
        {
            if(this.getCurPartner()==null)
                return null;
            if(this.getCurPartner() instanceof Kid)
                return (Kid) this.getCurPartner();
        }
        return null;
    }
}
