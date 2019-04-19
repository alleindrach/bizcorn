package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IMessage;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection="Messages")
@CompoundIndexes({
        @CompoundIndex(name = "session_index", def = "{'sessionId', 1, 'createDate': -1}")

})
public class Message implements IMessage {
    @Id
    private String id;

    private String sessionId;//null 离线消息，

    private String groupId;//null 非群消息

    private String srcId;//发送方id

    private String destId;//接收方id，null 为群消息，

    private String content;

    private Date createDate;

    private Integer status;//0=未送达， 1=已送达， 2=已阅读

    private Date deliverDate;//送达时间

    private Date copyDate;//阅读时间

    private Integer msgType=0;//0:p2p,1:p2group ,2:sysbroadcast

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Date getCopyDate() {
        return copyDate;
    }

    public void setCopyDate(Date copyDate) {
        this.copyDate = copyDate;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
