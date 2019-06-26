/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

public enum StoryType {
    SOUND(0),SLIDE(1);
    private int ctype;
    private StoryType(int ctype){
        this.ctype=ctype;
    }
    public int getValue(){
        return this.ctype;
    }
    public void setValue(int val){
        this.ctype=val;
    }
}
