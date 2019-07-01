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
    private User owner;//所有人
    @Getter
    @Setter
    private String picture;
    @DBRef
    @Getter
    @Setter
    private User operator;//当前的操作方

    @DBRef
    @Getter
    @Setter
    private Integer operation=0;//当前的操作

    @Getter
    @Setter
    private Date createDate;
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
    @DBRef
    private Frame frame;//相框
    @Getter
    @Setter
    @DBRef
    private PackageBox packageBox;//包装盒
    @Getter
    @Setter
    private Integer status;//状态

//    @Override
//    public void write(JSONSerializer serializer, Object fieldName, Type fieldType, int features) throws IOException {
//
//    }
}
