package allein.bizcorn.service.db.mongo.dao;



import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.mongo.Story;

import java.util.List;


public interface StoryDAO extends  BaseDAO<Story> {

    List<Story> selectByUid(String uid);
    Boolean isSotryIncludeFileExists(String fid);
}
