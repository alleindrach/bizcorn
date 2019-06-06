/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-31 11:36
 **/
public class Filter implements Serializable{
    @Getter
    @Setter
    private String key;
    @Getter
    @Setter
    private String op;
    @Getter
    @Setter
    private Object val;
    public Filter()
    {

    }
    public Filter(String key,String op,Object value)
    {
        this.key=key;
        this.val=value;
        this.op=op;
    }
}
