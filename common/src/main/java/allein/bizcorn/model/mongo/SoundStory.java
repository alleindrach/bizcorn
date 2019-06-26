/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import allein.bizcorn.common.util.UrlUtil;
import allein.bizcorn.common.websocket.Action;
import allein.bizcorn.model.facade.IMessage;
import allein.bizcorn.model.facade.IStory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;


//@CompoundIndexes({
//        @CompoundIndex(name = "session_index", def = "{'sessionId', 1, 'createDate': -1}")
//
//})
public class SoundStory extends Story  //,JSONSerializable
{

    @Setter @Getter
    protected String snd;

    public SoundStory()
    {
        this.type=StoryType.SOUND;
    }

    @Override
    public Object getData() {
        return snd;
    }

    @Override
    public void setData(Object v) {
        this.snd= (String) v;
    }

    public String toString(String filebase){

        SoundStory story=this;
        JSONObject jsonObject=new JSONObject ();//JSONObject) JSON.toJSON(story);
        jsonObject.put("id",this.getId());
        jsonObject.put("author",this.getTalker().getId());
        jsonObject.put("channel",this.getChannel());
        jsonObject.put("createTime",this.getCreateDate());
        jsonObject.put("downloads",this.getDownloads());
        jsonObject.put("likes",this.getLikes());
        jsonObject.put("published",this.isPublished());
        jsonObject.put("description",this.getDesc());
        jsonObject.put("title",this.getName());
        JSONArray scenes;
        scenes = new JSONArray();

        jsonObject.put("snd",snd);


//            if(story.getSceneList()!=null&& story.getSceneList().size()>0){
//                story.getSceneList().forEach(scene->{
//                    scene.setSoundSource(this.filebase+scene.getSoundSource());
//                    scene.setImageSource(this.filebase+scene.getImageSource());
//                });
//            }
        String result=jsonObject.toJSONString();// JSON.toJSONString(story);

        return result;

    }
}
