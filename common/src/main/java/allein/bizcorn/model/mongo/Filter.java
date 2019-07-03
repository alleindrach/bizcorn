/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @JSONField(serialize = false)
    private List<Filter> chain=new ArrayList();
    public Filter()
    {

    }
    public Filter(String key,String op,Object value)
    {
        this.key=key;
        this.val=value;
        this.op=op;
    }
    public static Filter From(String key,String op,Object value){
        return new Filter(key,op,value);
    }
    public Filter Combine(Filter another){
        chain.add(another);
        return this;
    }
    public  Filter Combine(String op, Filter another){
//        JSONObject filter=new JSONObject();
//        filter.put("op",op);
        JSONArray subFilters=new JSONArray();
        subFilters.add(JSON.toJSON(this));
        subFilters.add(JSON.toJSON(another));
//        filter.put("val",subFilters);
        return Filter.From("",op,subFilters);
    }
    public  JSONArray toJSONArray(){
        JSONArray jsonArray=new JSONArray();
        for (Filter f:chain
                ) {
            jsonArray.add(JSON.toJSON(f));
        }
        return jsonArray;
    }
    public static JSONObject ToQueryParam(int from ,int size,Filter filter ,Sorter sorter){
        JSONObject param=new JSONObject();
        param.put("from",from);
        param.put("size",size);
        param.put("filters",filter.toJSONArray());
        param.put("sorters",sorter.toJSONArray());
        return param;
    }
}
