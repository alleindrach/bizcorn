/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.websocket;

public enum Action {
    //声音变变变消息到达服务器，服务器->接收方，content：story
    SOUND_ARRIVED("sound.arrived"),
    //声音变变变消息到达服务器,通知到接收方后，服务器->发送方 content:story
    SOUND_ACK("sound.ack"),
    //接收方声音变变变消息阅读后，发送给发送方的消息，接收方->发送方,content:{
//    待定
//}
    SOUND_COPIED("sound.copied"),

    //超级变变变消息到达服务器,服务器->接收方，content:story
    SLIDE_ARRIVED("slide.arrived"),
    //声音变变变消息到达服务器,通知到接收方后，服务器->发送方 content:story
    SLIDE_ACK("slide.ack"),
    //超级变变变消息被接收方接收后，接收方->发送方,发送方可以开始甩动控制了,content 待定
    SLIDE_COPIED("slide.copied"),
    //超级变变变发送方->接收方，开始显示第一帧,content:storyid
    SLIDE_START("slide.start"),
    //超级变变变一方发给另一方，显示下一帧,content:storyid,nextindex
    SLIDE_NEXT("slide.next"),SLIDE_BACK("slide.back"),
//    绑定发起方（二维码的扫描方），发起绑定后,服务器->被绑定方,content:bind方的名称，tokenid
    BIND_REQURIE("bind.require"),
    //    被绑定方（二维码的出示方），确认绑定后,服务器->绑定方,content:被绑定方的名称，tokenid
    BIND_ACK("bind.ack"),
//    通过服务器中转发给绑定方的消息，确认送达，服务器->发送方,content{
//        origin:原始消息,
//        target:接收方,
//
//    }
    GIFT_ACTION("gift.action"),
    MESSAGE_ACK("message.ack");


    private String action;
    private Action(String action){
        this.action=action;
    }
    public String getValue(){
        return this.action;
    }
    public void setValue(String val){
        this.action=val;
    }
}
