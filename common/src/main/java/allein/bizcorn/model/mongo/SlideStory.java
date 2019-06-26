package allein.bizcorn.model.mongo;

import allein.bizcorn.common.util.UrlUtil;
import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.facade.IUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

public class SlideStory extends Story {
    @Getter
    @Setter
    private List<Scene> scenes;

    public SlideStory()
    {
        this.type=StoryType.SLIDE;
    }

    public String toString(String filebase){

        SlideStory story=this;
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
        this.getScenes().forEach(
                scene-> {
                    JSONObject jsonScene = new JSONObject();

                    String imageSource = scene.getImg();
                    String soundSource = scene.getSnd();
                    if (!UrlUtil.isUrl(imageSource)) {
                        imageSource = filebase + imageSource;
                    }
                    if (!UrlUtil.isUrl(soundSource)) {
                        soundSource = filebase + soundSource;
                    }
                    jsonScene.put("img", imageSource);
                    jsonScene.put("snd", soundSource);
                    scenes.add(jsonScene);
                }
        );
        jsonObject.put("scenes",scenes);


//            if(story.getSceneList()!=null&& story.getSceneList().size()>0){
//                story.getSceneList().forEach(scene->{
//                    scene.setSoundSource(this.filebase+scene.getSoundSource());
//                    scene.setImageSource(this.filebase+scene.getImageSource());
//                });
//            }
        String result=jsonObject.toJSONString();// JSON.toJSONString(story);

        return result;

    }

    @Override
    public Object getData() {
        return scenes;
    }

    @Override
    public void setData(Object v) {
        this.scenes= (List<Scene>) v;
    }
}
