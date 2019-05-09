/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.common.misc;

public enum MetaData {
    CONTENT_TYPE("_contentType"),SUFFIX("_fileSuffix"),ORIGIN_FILENAME("_originFileName");
    private String ctype;
    private MetaData(String ctype){
        this.ctype=ctype;
    }
    public String getValue(){
        return this.ctype;
    }
    public void setValue(String val){
        this.ctype=val;
    }
}
