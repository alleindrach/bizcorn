package allein.bizcorn.model.mongo;

import allein.bizcorn.common.util.UrlUtil;
import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.facade.IUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection="Story")
public class Story implements IStory {
    @Id
    private String id;
    @DBRef
    private IUser author;
    private List<Scene> scenes;
    private List<Tag> tags;
    private String channel;
    private Date createTime;
    private Integer downloads;
    @DBRef
    private List<Comment> comments;
    private Integer likes;
    private Integer published;
    private String title;
    private String description;


    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPublished() {
        return published;
    }

    public void setPublished(Integer published) {
        this.published = published;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IUser getAuthor() {
        return author;
    }

    public void setAuthor(IUser author) {
        this.author = author;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String toString(String filebase){

            Story story=this;
            JSONObject jsonObject= (JSONObject) JSON.toJSON(story);
            jsonObject.put("author",story.getAuthor().getId());
////            story.getAuthor().setPassword("*");
////            story.getAuthor().setMobile("*");
            JSONArray scenes=jsonObject.getJSONArray("scenes");
            scenes.forEach(
                    scene-> {
                        String imageSource=((JSONObject) scene).getString("imageSource");
                        String soundSource=((JSONObject) scene).getString("soundSource");
                        if(!UrlUtil.isUrl(imageSource))
                        {
                            imageSource=filebase+imageSource;
                        }
                        if(!UrlUtil.isUrl(soundSource))
                        {
                            soundSource=filebase+soundSource;
                        }
                        ((JSONObject) scene).put("img",imageSource);
                        ((JSONObject) scene).put("snd",soundSource);
                    }
            );
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
