/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

public enum GiftActionType {
    TAKE_PICTURE(0),CHOOSE_FRAME(1),RECORD_VOICE(4),CHOOSE_BOX(5),RELEASE(6);
    private int status;
    private GiftActionType(int status){
        this.status=status;
    }
    public int getValue(){
        return this.status;
    }
    public void setValue(int val){
        this.status=val;
    }
}
