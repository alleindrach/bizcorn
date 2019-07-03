/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-31 11:36
 **/
public class Sorter implements Serializable{
    @Getter
    @Setter
    private String key;
    @Getter
    @Setter
    private String dir;
    @JSONField(serialize = false)
    private List<Sorter> chain=new ArrayList();
    public Sorter()
    {
        chain.add(this);
    }
    public Sorter(String key)
    {
        this.key=key;
        this.dir="asc";
        chain.add(this);
    }
    public Sorter(String key, String dir)
    {
        this.key=key;
        this.dir="desc";
        chain.add(this);
    }
    public static Sorter From(String key, String dir){
        return new Sorter(key,dir);
    }
    public Sorter combine(Sorter another)
    {
        chain.add(another);
        return this;
    }
    public  JSONArray toJSONArray(){
        JSONArray jsonArray=new JSONArray();
        for (Sorter s:chain
             ) {
            jsonArray.add(JSON.toJSON(s));
        }
        return jsonArray;
    }
}
