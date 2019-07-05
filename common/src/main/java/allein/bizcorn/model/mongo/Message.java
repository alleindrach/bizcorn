package allein.bizcorn.model.mongo;

import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.model.facade.IMessage;
import allein.bizcorn.model.output.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="Message")
//@CompoundIndexes({
//        @CompoundIndex(name = "session_index", def = "{'sessionId', 1, 'createDate': -1}")
//
//})
public class Message implements IMessage {
    @Id
    @Getter
    @Setter
    private String id;

//    private String sessionId;//null 离线消息，

//    private String groupId;//null 非群消息
//@Getter
//@Setter
//    private String srcId;//发送方id
//    @Getter
//    @Setter
//    private String destId;//接收方id，null 为群消息，
    @Getter
    @Setter
    private String srcName;
    @Getter
    @Setter
    private String destName;
    @Getter
    @Setter
    private String content;
    @Getter
    @Setter
    private Action action;
    @Getter
    @Setter
    private Date createDate;
    @Getter
    @Setter
    private MessageStatus status;//0=服务中心已收到， 1=接收方已阅读
    @Getter
    @Setter
    private Date deliverDate;//送达时间
    @Getter
    @Setter
    private Date copyDate;//阅读时间
    @Getter @Setter
    private ContentType contentType=ContentType.SOUND_STORY;
    static public  Message BindRequireMessage(BindToken token){
        Message msg=new Message();
        msg.setDestName(token.getBindee().getUsername());
        msg.setSrcName(token.getBinder().getUsername());
        msg.setContentType(ContentType.CONFIRM_TOKEN);
        msg.setContent(token.getId());
        msg.setAction(Action.BIND_REQURIE);
        msg.setCreateDate(token.getCreateDate());
        msg.setStatus(MessageStatus.INIT);
        return msg;
    }
    static public  Message BindAckMessage(BindToken token,Result result){
        Message msg=new Message();
        msg.setSrcName(token.getBindee().getUsername());
        msg.setDestName(token.getBinder().getUsername());
        msg.setContentType(ContentType.RESULT);
        msg.setContent(JSON.toJSONString(result,SerializerFeature.DisableCircularReferenceDetect));
        msg.setAction(Action.BIND_ACK);
        msg.setCreateDate(token.getCreateDate());
        msg.setStatus(MessageStatus.INIT);
        return msg;
    }
    static public  Message StoryArrivedMessage(Story message){
        Message msg=new Message();
        msg.setDestName(message.getTalkee().getUsername());
        msg.setSrcName(message.getTalker().getUsername());
        msg.setContentType(message.type==StoryType.SLIDE? ContentType.SLIDE_STORY:ContentType.SOUND_STORY);
        msg.setContent(JSON.toJSONString(message, SerializerFeature.DisableCircularReferenceDetect));
        msg.setAction(message.type==StoryType.SLIDE?Action.SLIDE_ARRIVED:Action.SOUND_ARRIVED);
        msg.setCreateDate(message.getCreateDate());
        msg.setStatus(MessageStatus.INIT);
        return msg;
    }

    static public  Message StoryAckMessage(Story message){
        Message msg=new Message();
        msg.setDestName(message.getTalker().getUsername());
        msg.setSrcName(message.getTalkee().getUsername());
        msg.setContentType(message.type==StoryType.SLIDE? ContentType.SLIDE_STORY:ContentType.SOUND_STORY);
        msg.setContent(JSON.toJSONString(message,SerializerFeature.DisableCircularReferenceDetect));
        msg.setAction(message.type==StoryType.SLIDE?Action.SLIDE_ACK:Action.SOUND_ACK);
        msg.setCreateDate(message.getCreateDate());
        msg.setStatus(MessageStatus.INIT);
        return msg;
    }
    static public  Message AckMessage(JSONObject message,User sender){
        Message msg=new Message();
        msg.setDestName(sender.getUsername());
        msg.setSrcName("Center");
        msg.setContentType(ContentType.ACK);

        JSONObject ack=new JSONObject();
        ack.put("origin",message);
        ack.put("target", JSON.toJSON( sender.getCurPartner()));
//        ack.put("sendTime",System.currentTimeMillis());
        msg.setContent(JSON.toJSONString(ack,SerializerFeature.DisableCircularReferenceDetect));
        msg.setAction(Action.MESSAGE_ACK);
        msg.setCreateDate(new Date());
        msg.setStatus(MessageStatus.INIT);
        return msg;
    }

    static public  Message GiftMessage(Gift gift,String from ,String to){
        Message msg=new Message();
//
        msg.setDestName(from);
        msg.setSrcName(to);
        msg.setContentType(ContentType.GIFT);
        msg.setAction(Action.GIFT_ACTION);
        msg.setCreateDate(gift.getCreateDate());
        msg.setContent(JSON.toJSONString(gift,SerializerFeature.DisableCircularReferenceDetect));
        msg.setStatus(MessageStatus.INIT);
        return msg;
    }
}
