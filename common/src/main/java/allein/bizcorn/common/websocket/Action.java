/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.websocket;

public enum Action {
    SOUND_ARRIVED("sound.arrived"),SOUND_COPIED("sound.copied"),
    BIND_REQURIE("bind.require"),BIND_ACK("bind.ack");
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
