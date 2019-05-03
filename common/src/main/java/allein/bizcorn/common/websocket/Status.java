/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.websocket;

public enum Status {
    ON_LINE(0),OFF_LINE(1);
    private int status;
    private Status(int status){
        this.status=status;
    }
    public int getValue(){
        return this.status;
    }
    public void setValue(int val){
        this.status=val;
    }
}
