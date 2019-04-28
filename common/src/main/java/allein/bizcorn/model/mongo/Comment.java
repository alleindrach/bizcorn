package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IComment;
import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="Comment")
public class Comment implements IComment {
    @DBRef
    private IStory story;
    @DBRef
    private IUser author;
    private Date createTime;
    private String content;

    public IStory getStory() {
        return story;
    }

    public void setStory(IStory story) {
        this.story = story;
    }

    public IUser getAuthor() {
        return author;
    }

    public void setAuthor(IUser author) {
        this.author = author;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
