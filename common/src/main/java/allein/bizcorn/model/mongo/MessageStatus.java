/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

public enum MessageStatus {
    INIT(0),DISPATCHED(1),COPIED(4);
    private int status;
    private MessageStatus(int status){
        this.status=status;
    }
    public int getValue(){
        return this.status;
    }
    public void setValue(int val){
        this.status=val;
    }
}
