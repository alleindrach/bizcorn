package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.facade.IStory;
import allein.bizcorn.model.mongo.Story;
import allein.bizcorn.model.mongo.User;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
public class StoryDAOImpl extends   BaseDAOImpl<Story> implements StoryDAO  {


    @Override
    public List<IStory> selectByUid(String uid) {
        return this.find(new Query(where("author.id").is(uid)));
    }
}
