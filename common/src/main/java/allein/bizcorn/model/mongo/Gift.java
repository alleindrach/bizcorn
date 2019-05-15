package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IToy;
import allein.bizcorn.model.facade.IUser;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

@Document(collection="Gift")
public class Gift implements Serializable{
    @Id
    @Getter
    @Setter
    private String id;
    @DBRef
    @Getter
    @Setter
    private User owner;
    @Getter
    @Setter
    private Date createTime;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String desc;
    @Getter
    @Setter
    private String sndMsg;//留言
    @Getter
    @Setter
    private String frame;//相框
    @Getter
    @Setter
    private String packageBox;//包装盒
    @Getter
    @Setter
    private Integer status;//状态

//    @Override
//    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
//
//    }
}
