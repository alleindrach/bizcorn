package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IBundle;
import allein.bizcorn.model.facade.IPointLog;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
@Document(collection="PointLog")
public class PointLog implements IPointLog{
    @Id
    private String id;
    @DBRef
    private IUser owner;
    private String note;
    private Integer direction;//=1 收入，=-1 支出
//    private Integer deposit;//期初
//    private Integer balance;//结余
    private Date createDate;
    private Integer status;//0=未处理，1=已记账
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IUser getOwner() {
        return owner;
    }

    public void setOwner(IUser owner) {
        this.owner = owner;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
