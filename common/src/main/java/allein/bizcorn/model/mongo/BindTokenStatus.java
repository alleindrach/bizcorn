/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

public enum BindTokenStatus {
    INIT(0),FIRED(1),CONFIRMED(2),EXPIRED(4);
    private int status;
    private BindTokenStatus(int status){
        this.status=status;
    }
    public int getValue(){
        return this.status;
    }
    public void setValue(int val){
        this.status=val;
    }
}
