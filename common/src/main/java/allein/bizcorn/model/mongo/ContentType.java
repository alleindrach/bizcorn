/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

public enum ContentType {
    SOUND_MESSAGE(0),SLIDE_STORY(1),CONFIRM_TOKEN(2),RESULT(3),PLAIN_TEXT(99);
    private int ctype;
    private ContentType(int ctype){
        this.ctype=ctype;
    }
    public int getValue(){
        return this.ctype;
    }
    public void setValue(int val){
        this.ctype=val;
    }
}
