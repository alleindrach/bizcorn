package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IComment;
import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.facade.IUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="Comment")
public class Comment implements IComment {
    @DBRef(lazy = true)
    @Getter
    @Setter
    private IStory story;
    @DBRef(lazy = true)
    @Getter
    @Setter
    private IUser author;
    @Getter
    @Setter
    private Date createTime;
    @Getter
    @Setter
    private String content;

}
