/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.mq;

public enum Topic {
    DEFAULT("default"),USER_LOGIN("user.login"),IMAGE_THUMB("image.thumb"),
    USER_WS_CONNECTED("user.ws.online"),USER_WS_DISCONNECTED("user.ws.offline"),
    USER_WS_MESSAGE("user.ws.message"),USER_SMS_SENDDING("user.sms.sendding");
    private String topic;
    private Topic(String val){
        this.topic=val;
    }
    public String getValue(){
        return this.topic;
    }
    public void setValue(String val){
        this.topic=val;
    }

}
