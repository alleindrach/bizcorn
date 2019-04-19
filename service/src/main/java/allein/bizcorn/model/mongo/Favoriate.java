package allein.bizcorn.model.mongo;

import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.facade.IFavoriate;
import allein.bizcorn.model.facade.IUser;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

public class Favoriate implements IFavoriate {
    @DBRef
    private IUser favoriator;
    @DBRef
    private IStory story;
    private Date createTime;

}
