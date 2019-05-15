package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IPointLog;
import allein.bizcorn.model.facade.IUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="PointLog")
public class PointLog implements IPointLog{
    @Id
    @Getter
    @Setter
    private String id;
    @DBRef
    @Getter
    @Setter
    private IUser owner;
    @Getter
    @Setter
    private String note;
    @Getter
    @Setter
    private Integer direction;//=1 收入，=-1 支出
//    private Integer deposit;//期初
//    private Integer balance;//结余
@Getter
@Setter
    private Date createDate;
    @Getter
    @Setter
    private Integer status;//0=未处理，1=已记账

}
